package jiayiwei.essay_ui

import javax.swing.{JButton, JLabel}

import rx.{Ctx, Rx}

object RxJComponent {
  class RxJLabel(text: Rx[String], implicit val ctx: Ctx.Owner) extends JLabel{
    text.trigger{
      setText(text.now)
    }
  }
  
  class RxButton(text: Rx[String], implicit val ctx: Ctx.Owner) extends JButton{
    text.trigger{
      setText(text.now)
    }
  }
}
