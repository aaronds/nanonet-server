package uk.co.madsie.nanonet.server.messages
import uk.co.madsie.nanonet.server._

abstract class HostMessage;

case class AddHost(host : NanoHost) extends HostMessage;
case class RemHost(host : NanoHost) extends HostMessage;
case class SetHosts(hostList : List[NanoHost]) extends HostMessage;
case class RemHostByAddress(host : NanoHost) extends HostMessage;
case class ReplaceHostByAddress(host : NanoHost) extends HostMessage;

// vim: set ts=4 sw=4 et:
