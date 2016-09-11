package jiayiwei

import java.awt.event.{ActionEvent, ActionListener}

/**
 * Created by weijiayi on 9/9/16.
 */
package object essay_ui {
  type CallBack = () => Unit

  def callback(action: =>Unit) = () => action

  def mkAction(action: =>Unit) = new ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = action
  }
}
