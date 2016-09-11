package jiayiwei.essay_ui

import javax.swing.{JLabel, JFrame}

import jiayiwei.essay.{GUIConfig, EssayStat}

/**
 * Created by weijiayi on 9/9/16.
 */
object UIMain {

  def main(args: Array[String]) {
    MainFrame.mkMainFrameByConfigFile("").start()
  }
}

object UITest{
  def main(args: Array[String]) {
    MainFrame.mkMainFrameByConfigFile(EssayStat.sample).start()
  }
}