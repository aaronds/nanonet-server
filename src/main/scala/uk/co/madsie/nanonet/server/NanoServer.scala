package uk.co.madsie.nanonet.server
import scala.actors._,Actor._
import concurrent.ops._
import uk.co.madsie.nanonet.util._
import uk.co.madsie.nanonet.server.messages._

class NanoServer(val name : String,val addressLength : Int,val dataLength : Int,val identity : NanoAddress) extends Actor {

	def act = {
		actLoop(new NanoServerConfig(List[NanoInterface](),List[NanoHost]()))
	}

	def routePacket(config : NanoServerConfig,p : NanoPacket) : Unit = {
		/* Serch hosts, then other interfaces*/
		val dest = config.hosts.find(p.to equals _.address).orElse(config.interfaces.find(p.to equals _.address))

		dest match {
			/* If its destined for a host call service*/
			case Some(h:NanoHost) => h.service(p)
			/* If another interface message its actor*/
			case Some(i:NanoInterface) => i ! Send(p)
			case None => {}
		}
	}

	def actLoop(config : NanoServerConfig) {
		/*
		 * TODO: Remove differntiation between receive and send.
		 */
		react {
			case Receive(p) => {
				routePacket(config,p)
				actLoop(config)
			}
			case Send(p) => {
				routePacket(config,p)
				actLoop(config)
			}
			case Open(intf) => {

				/*Ensure interface addresses are unique, if two confilict the newer one superseeds*/
				config.interfaces.filter(_.address equals intf.address).foreach(_.end())
				config.interfaces.filter(_.address equals intf.address).foreach(_ ! Exit)

				/* Interfaces are actors start or restart */

				intf.getState match {
					case Actor.State.New => intf.start
					case Actor.State.Terminated => intf.restart
					case _ => 
				}

				/* Add the interface to the active server config.*/

				actLoop(new NanoServerConfig(intf :: config.interfaces.filter((i : NanoInterface) => !(intf.address equals i.address)),config.hosts))
			}
			case Close(intf) => {
				/* Remove interface from active config */
				actLoop(new NanoServerConfig(config.interfaces.filter(_ != intf),config.hosts))
			}
			case Status() => {
				/* Fetch the current config - only used for testing */
				reply(config)
				actLoop(config)
			}
			case Exit => {
				config.hosts.foreach(_.end)
				config.interfaces.foreach(_ ! Exit)
				exit
			}
			case m : HostMessage => {actLoop(handleHost(config,m))}
			case _ => actLoop(config)
		}
	}

	/**
	 * Deal with messages relating to hosts
	 */

	def handleHost(config : NanoServerConfig,m : HostMessage) : NanoServerConfig = m match {
		case AddHost(host) => {
			host.begin()
			new NanoServerConfig(config.interfaces,host :: config.hosts)
		}
		case RemHost(host) => {
			config.hosts.filter(_ == host).foreach(_.end())
			new NanoServerConfig(config.interfaces,config.hosts.filter(_ != host))
		}
		case SetHosts(hostList) => {
			/*Replace all hosts*/
			config.hosts.foreach(_.end())
			hostList.foreach(_.begin())
			new NanoServerConfig(config.interfaces,hostList)
		}
		case RemHostByAddress(host) => {
			config.hosts.filter((h : NanoHost) => h.address equals host.address).foreach(_.end())
			host.begin()
			new NanoServerConfig(config.interfaces,config.hosts.filter((h : NanoHost) => !(h.address equals host.address)))
		}
		case ReplaceHostByAddress(host) => {
			config.hosts.filter((h : NanoHost) => h.address equals host.address).foreach(_.end())
			host.begin()
			new NanoServerConfig(config.interfaces,host :: config.hosts.filter((h : NanoHost) => !(h.address equals host.address)))
		}
		case _ => config
	}

	/*
	 * Send method, a helper for NanoPorts
	 */

	def send(toAddress : NanoAddress,from : NanoHost,toPort : Byte,fromPort : Byte,data : Array[Byte]) = {
		this ! Send(new NanoPacket(toAddress,from.address,fromPort,toPort,data.length,data))
	}
}

// vim: set ts=4 sw=4 et:
