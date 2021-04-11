import org.apache.zookeeper.Watcher.Event.EventType.{NodeChildrenChanged, NodeCreated, NodeDeleted}
import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}

import scala.jdk.CollectionConverters._

case class ZNodeWatcher(zk: ZooKeeper,
                        znode: String,
                        znodeListener: ZNodeListener
                       ) extends Watcher {
  
  zk.exists(znode, this)
  
  override def process(event: WatchedEvent): Unit = {
    
    event.getType match {
      case NodeCreated => handleNodeCreated()
      case NodeDeleted => handleNodeDeleted()
      case _           => ()
    }
    
    zk.exists(znode, this)
  }
  
  def handleNodeCreated(): Unit = {
    ZNodeChildrenWatcher(zk, znode, handleChildrenChanged)
    znodeListener.znodeCreated()
  }

  def handleNodeDeleted(): Unit = {
    znodeListener.znodeDeleted()
  }
  
  def handleChildrenChanged(children: List[String]): Unit = {
    znodeListener.childrenChanged(children) 
  }
}

case class ZNodeChildrenWatcher(zk: ZooKeeper,
                                znode: String,
                                onChildrenChanged: (List[String]) => Unit) extends Watcher {
  registerWatch(znode)
  
  override def process(event: WatchedEvent): Unit = {
    event.getType match {
      case NodeChildrenChanged => 
        registerWatch(event.getPath)
        val children = gatherChildren(znode)
        
        onChildrenChanged(children)
      case _ => ()
    }
  }
  
  def nodeExists(node: String): Boolean = {
    return zk.exists(node, false) != null
  }
  
  def gatherChildren(node: String): List[String] = {
    if nodeExists(znode) then
      val children = zk.getChildren(node, false)
        .asScala
        .toList
        .map(x => s"$node/$x")
      
      children.foldLeft(children)((xs, x) => xs ++ gatherChildren(x))
    else Nil
  }
  
  def registerWatch(node: String): List[String] = {
    if nodeExists(node) then
      val children = zk.getChildren(node, this)
        .asScala
        .toList
        .map(x => s"$node/$x")
      
      children.foldLeft(children)((xs, x) => xs ++ registerWatch(x))
    else Nil
  }

}
