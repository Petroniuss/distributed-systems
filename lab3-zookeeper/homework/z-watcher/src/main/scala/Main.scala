import org.apache.zookeeper.ZooKeeper

@main def hello(hostPort: String, exec: String*): Unit = {
  val znode = "/z"
  val zk = new ZooKeeper(hostPort, 3000, null)
  val listener = new ZNodeListenerImpl(znode, exec.toArray)
  val watcher = ZNodeWatcher(zk, znode, listener)
  
  Thread.sleep(1000000)
}


