package jiayiwei.essay

import java.awt.{Dimension, Font}
import java.io.FileWriter
import javax.swing.{UIManager, JOptionPane}

import scala.io.Source

/**
 * Created by weijiayi on 9/8/16.
 */
object LoadFile {
  def load(fileName: String) = {
    loadOpt(fileName) match{
      case Some(lines) => lines
      case None =>
        JOptionPane.showMessageDialog(null,
          s"the file '$fileName' cannot be loaded.",
          "File not loaded",
          JOptionPane.ERROR_MESSAGE)
        throw new Exception(s"Can't load file '$fileName'")
    }
  }

  def loadOpt(fileName: String) = {
    try{
      Some(Source.fromFile(fileName).getLines())
    }catch {
      case _: Exception =>
        None
    }
  }

  val standard = "standard-data"
}


case class GUIConfig(editorFont: Font, editorDimension: Dimension){
  def configText = {
    Seq(
      s"font-name: ${editorFont.getFontName}",
      s"font-size: ${editorFont.getSize}",
      s"editor-dimension: ${editorDimension.width},${editorDimension.height}"
    ).mkString("\n")
  }
}

object GUIConfig{
  val defaultConfig = GUIConfig(
    editorFont = UIManager.getDefaults.getFont("TextPane.font"),
    editorDimension = new Dimension(600,500)
  )
  
  def mapFromLines(lines: Seq[String]) = {
    var map = Map[String, String]()
    for{
      l <- lines if l.trim.nonEmpty
      line = l.trim
    } {
      val parts = line.split(":\\s*")
      map += (parts(0) -> parts(1))
    }
    map
  }
  
  def loadFromFile(path: String = "gui-config.txt"): GUIConfig = {
    LoadFile.loadOpt(path) match{
      case Some(lines) =>
        try{
          val map = mapFromLines(lines.toSeq)
          val font = new Font(map("font-name"),Font.PLAIN ,map("font-size").toInt)
          val d = map("editor-dimension").split(",")
          val dimension = new Dimension(d(0).toInt, d(1).toInt)
          GUIConfig(font, dimension)
        } catch{
          case e: Exception =>
            JOptionPane.showMessageDialog(null,
              s"the file '$path' cannot be parsed.",
              "File can't be parsed.",
              JOptionPane.ERROR_MESSAGE)
            throw e
        }

      case None =>
        val text = defaultConfig.configText
        val fw = new FileWriter(path)
        fw.write(text)
        fw.close()
        JOptionPane.showMessageDialog(null,
          s"the file '$path' cannot be found, a default configuration is created.",
          "Config not found",
          JOptionPane.WARNING_MESSAGE)
        defaultConfig
    }
  }
}
