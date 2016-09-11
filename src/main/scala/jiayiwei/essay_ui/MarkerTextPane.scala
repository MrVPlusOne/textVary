package jiayiwei.essay_ui

import java.awt._
import javax.swing.JTextPane
import javax.swing.event.{DocumentEvent, DocumentListener}
import MarkType.withAlpha

import jiayiwei.essay.{GUIConfig, TextRange}

trait MarkType{
  def mark(r1: Rectangle, r2: Rectangle, g: Graphics)
}

object MarkType{
  def withAlpha(c: Color, a: Double): Color = new Color(c.getRed, c.getGreen, c.getBlue, (a*255).toInt)

  def IntInterpolate(y0: Int, y1: Int)(x: Double) = y0 + ((y1-y0)*x).toInt

  def colorInterpolate(c0: Color, c1: Color)(x: Double) = {
    new Color(
      IntInterpolate(c0.getRed,c1.getRed)(x),
      IntInterpolate(c0.getGreen,c1.getGreen)(x),
      IntInterpolate(c0.getBlue,c1.getBlue)(x),
      IntInterpolate(c0.getAlpha,c1.getAlpha)(x)
    )
  }
}

case class ColorBlock(color: Color) extends MarkType{
  override def mark(r1: Rectangle, r2: Rectangle, g: Graphics): Unit = {
    g.setColor(color)
    g.fillRect(r1.x,r2.y,r2.x-r1.x,r1.height)
  }
}

object ColorBlock {
  import MarkType._

//  def colorFromFrequency(f: Int) = {
//    if(f<=3) withAlpha(Color.green, 0.15)
//    else if(f<=6) withAlpha(Color.yellow, 0.3)
//    else withAlpha(Color.orange,0.4)
//  }

  def colorFromFrequency(f: Int) = {
    val x = 1.0 - math.exp(-(f-2).toDouble/3)
    colorInterpolate(
      withAlpha(Color.yellow, 0.3),
      withAlpha(Color.red, 0.0))(x)
  }
}

case class ColorUnderline(color: Color, thickness: Float) extends MarkType{
  override def mark(r1: Rectangle, r2: Rectangle, g: Graphics): Unit = {
    val g2d = g.asInstanceOf[Graphics2D]
    g2d.setColor(color)
    val y = r1.y+r1.height
    g2d.setStroke(new BasicStroke(thickness))
    g2d.drawLine(r1.x, y, r2.x, y)
  }
}

object ColorUnderline{
  def colorFromFrequency(f: Int) = {
//    if(f<=3) withAlpha(Color.green, 1.0)
//    else if(f<=6) withAlpha(Color.yellow, 1.0)
//    else withAlpha(Color.orange,1.0)
    Color.blue
  }
}

case class MarkRegion(range: TextRange, data: MarkType)

/**
 * This is a special JTextPane which can draw various color regions (see `MarkRegion` case class) on top of its text.
 */
class MarkerTextPane() extends JTextPane{
  private var currentMarkers = Seq[MarkRegion]()

  setFont(GUIConfig.loadFromFile().editorFont)

  def setMarkers(markers: Seq[MarkRegion]) = {
    currentMarkers = markers
    repaint()
  }

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)

    currentMarkers.foreach{
      case MarkRegion(range, markerT) =>
        for{
          r1 <- rectOfPos(range.start)
          r2 <- rectOfPos(range.until)
        }{
          if(r1.y != r2.y){
            val r1End = new Rectangle(getWidth, r1.y, 0, r1.height)
            val r2Start = new Rectangle(0, r2.y, 0, r2.height)
            markerT.mark(r1,r1End,g)
            markerT.mark(r2Start,r2,g)
          }else
            markerT.mark(r1,r2,g)
        }
    }
  }


  def rectOfPos(pos: Int): Option[Rectangle] = {
    try{
      val r = modelToView(pos)
      Some(r)
    }catch{
      case _: Exception =>
        println(s"[Warn] Can't access position $pos of the document!")
        None
    }
  }
  
  def lowerLeft(r: Rectangle) = (r.x, r.y+r.height)
}
