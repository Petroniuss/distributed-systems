trait ZNodeListener {
  def znodeCreated(): Unit
  
  def znodeDeleted(): Unit
  
  def childrenChanged(children: List[String]): Unit
}


class ZNodeListenerImpl(znode: String,
                        exec: Array[String]) extends ZNodeListener {
  
  var process: Option[Process] = None

  override def znodeCreated(): Unit = {
    try 
      println("Znode created!")
      process = Some(Runtime.getRuntime.exec(exec))
    catch
      case _ => println("failed to run program")
  }

  override def znodeDeleted(): Unit = {
    println("Znode deleted!")
    process match 
      case Some(prcs) =>
        prcs.destroy()
      case None => ()
    
    process = None
  }

  override def childrenChanged(children: List[String]): Unit = {
    println("--------------Update------------")
    println(s"Number of children: ${children.size}\n\n")
    children.sorted.foreach(println)
    println("-------------------------------\n\n")
  }
  
}
