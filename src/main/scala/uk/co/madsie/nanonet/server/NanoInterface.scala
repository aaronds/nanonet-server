package uk.co.madsie.nanonet.server
import scala.actors._,Actor._
import uk.co.madsie.nanonet.util._
import uk.co.madsie.nanonet.server.messages._

/**
 * Interfaces should extend from this class
 *
 * @see NanoSocketInterface
 * @see NanoTestInterface
 */

abstract class NanoInterface(val address : NanoAddress,server : NanoServer) extends Actor {
	def recv(timeout : Long) : NanoPacket;
	def send(p : NanoPacket) : Unit;
	def end() : Unit;

	def act {
		actLoop(server)
	}

	def actLoop(server : NanoServer) {
		react {
			case Receive(p) => {
				if(server != null){server ! Receive(p)}
				actLoop(server)
			}
			case Send(p) => {
				send(p)
				actLoop(server)
			}
			case Server(s) => actLoop(s)
			case Exit => {
				if(server != null){
					server ! Close(this)
				}
				end()
				exit
			}
			case _ => actLoop(server)
		}
	}
}

// vim: set ts=4 sw=4 et:
