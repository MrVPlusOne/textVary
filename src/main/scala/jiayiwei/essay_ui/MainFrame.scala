package jiayiwei.essay_ui

import java.awt.event.{AdjustmentEvent, AdjustmentListener, KeyAdapter}
import java.awt._
import javax.swing.event.{CaretEvent, CaretListener, DocumentEvent, DocumentListener}
import javax.swing._
import javax.swing.text.DefaultCaret

import jiayiwei.essay.{GUIConfig, ProjectSettings, EssayParser, EssayStat}
import jiayiwei.essay_ui.RxJComponent.{RxButton, RxJLabel}
import rx._
import rx.Ctx.Owner.Unsafe._

/**
 * The graphical user interface for textVary
 */
class MainFrame(initContent: String, editSize: Dimension, font: Font) {
  import MainFrame._
  
  val timeInSecVar = Var(0)
  val isTimeRunning = Var(false)

  new Timer(1000, mkAction{
    if(isTimeRunning.now)
      timeInSecVar.synchronized{
        timeInSecVar() = timeInSecVar.now + 1
      }
  }).start()

  val markerPane = new MarkerTextPane() {
    setText(initContent)
    setBorder(BorderFactory.createEmptyBorder(5,10,5,5))
  }

  val markerManager = new StatManager(markerPane, EssayStat.standard, EssayParser.standard, implicitly)

  val wordCountLabel = new RxJLabel(
    markerManager.wordCountVar.map{wc => s"  Word Count: $wc"}, implicitly)
  
  val timeLabel = new RxJLabel(
    timeInSecVar.map{ 
      t => s"  Time:  ${displayTimeFromSec(t)}"
    }, implicitly
  )
  
  val pauseButton = new RxButton(
    isTimeRunning.map{ r => if(r) "Pause" else "Resume"}, implicitly
  )
  pauseButton.addActionListener(mkAction{isTimeRunning() = !isTimeRunning.now})
  
  val resetButton = new JButton("Reset"){
    addActionListener(mkAction{
      timeInSecVar.synchronized{
        timeInSecVar() = 0
      }
    })
  }

  markerPane.getDocument.addDocumentListener(new DocumentListener {
    override def insertUpdate(e: DocumentEvent): Unit = markerManager.editCallBack()

    override def changedUpdate(e: DocumentEvent): Unit = markerManager.editCallBack()

    override def removeUpdate(e: DocumentEvent): Unit = markerManager.editCallBack()
  })

  markerPane.addCaretListener(new CaretListener {
    override def caretUpdate(e: CaretEvent): Unit = markerManager.caretCallBack()
  })

  /**
   * Call this method to start the JFrame
   * @return
   */
  def start() = {
    new JFrame(ProjectSettings.projectName){
      setContentPane(
        vContainer(
          hContainer(
            timeLabel, pauseButton, resetButton
          ),
          new JScrollPane(markerPane){
            setPreferredSize(editSize)
          },
          hContainer(
            wordCountLabel
          )
        )
      )

      pack()
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      setVisible(true)
    }
  }

  def hContainer(components: JComponent*) = {
    val box = Box.createHorizontalBox()
    components.foreach(box.add)
    box
  }

  def vContainer(components: JComponent*) = {
    val box = Box.createVerticalBox()
    components.foreach{c =>
      box.add(c)
      c.setAlignmentX(Component.LEFT_ALIGNMENT)
    }
    box
  }

  def hSpring = {
    new JPanel(){
      setMaximumSize(new Dimension(-1,1))
    }
  }
}

object MainFrame{
  def displayTimeFromSec(sec: Int): String = {
    "%02d:%02d:%02d".format(sec/3600, (sec/60)%60, sec%60)
  }

  def mkMainFrameByConfigFile(initContent: String) = {
    val config = GUIConfig.loadFromFile()
    new MainFrame(initContent, config.editorDimension ,config.editorFont)
  }
}
