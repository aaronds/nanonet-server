package uk.co.madsie.nanonet.util
import org.w3c.dom._

object NodeUtil {

	def nodeSetToList(obj : AnyRef) = {
		val nodeList = obj.asInstanceOf[NodeList]
		for(i <- 0 to nodeList.getLength - 1) yield nodeList.item(i).asInstanceOf[Element]
	}

	/**
	 * Take a sequence of dom elements and build a list of objects (subclasses of T) using the attribute specified by classAttribute passing the Element to the constructor, if no class is specified use default.
	 *
	 * List(<Element class = "com.blah.ClassA" />,<Element class = "com.blah.ClassB" />)
	 * is convered to:
	 * List(new ClassA(<Element class = "com.blah.ClassA" />),new ClassB(<Element class = "com.blah.ClassB" />))
	 */

	def constructObjectsByElement[T](elements : Iterable[Element],default : Class[_ <: T],classAttribute : String = "class",args :List[AnyRef] = List[AnyRef]()) : List[T] = {
		val argsClass = args.map(_.getClass)
		val defaultConstructor = default.getConstructor((classOf[Element] :: argsClass) : _*)

		(List[T]() /: elements) ((list,el) => if(el.hasAttribute(classAttribute)){
			try {
				val elClass = Class.forName(el.getAttribute(classAttribute))
				val constructor = elClass.getConstructor((classOf[Element] :: argsClass) : _*)
				constructor.newInstance((el :: args) : _*).asInstanceOf[T] :: list
			}catch{
				case e : Exception => {e.printStackTrace(); list}
				case _ => { list }
			}
		}else{
			defaultConstructor.newInstance((el :: args) : _*).asInstanceOf[T] :: list
		})
	}
}

// vim: set ts=4 sw=4 et:
