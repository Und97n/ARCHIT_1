package org.zizitop.arch

import java.awt.event.{MouseAdapter, MouseListener}
import java.awt._

import javax.swing._

class Window(val wwidth: Int, val wheight: Int, val wtitle: String,
             val wmouseListener: MouseListener = new MouseAdapter {}) extends JFrame(wtitle) {
  private val canvas = new WPanel


  private var command = false
  private var ready = false
  private var pc1 = false
  private var pc5 = false

  private val onButton = new JButton("Start")
  private val offButton = new JButton("Stop")
  private val timeSlider = new JSlider(0, 1000);

  setSize(wwidth + 5, wheight + 35)
//  setContentPane(canvas)
  val mainBox: Box = Box.createVerticalBox()
  val box: Box = Box.createHorizontalBox()
  box.add(onButton)
  box.add(offButton)
  box.add(timeSlider)
  mainBox.add(box)
  mainBox.add(canvas)
//  add(onOffButton)
  add(mainBox)
//  add(canvas)

  var timer: Thread = null

  onButton.addActionListener(e => {
    timer = new Thread(() => {
      var running = true

      def tick(): Unit = {
        canvas.repaint()
        canvas.repaint()
        try {
          Thread.sleep(timeSlider.getValue)
        } catch {
          case e: Throwable => running = false
        }
      }

      command = true
      ready = true
      pc5 = true
      tick()

      while(running) {
        pc1 = true
        tick()
        pc1 = false
        tick()
        command = !command
        tick()
      }

      command = false
      ready = false
      pc1 = false
      pc5 = false
      tick()
      pc1 = true
      tick()
      pc1 = false
      tick()
    })
    timer.start()
  })

  offButton.addActionListener(e => {
    timer.interrupt()
  })

  canvas.addMouseListener(wmouseListener)

  setLocationRelativeTo(null)
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  setVisible(true)

  private class WPanel extends JPanel {
    setSize(wwidth, wheight)

    def drawPP(gg: Graphics2D, name1: String, name2: String, n: Array[String]): Unit = {
      gg setColor Color.GRAY
      gg fillRect (250, 50, 100, 300)

      gg fillRect (190, 305, 30, 130)

      gg setColor (Color.RED )
      drawArrow(gg, 220, 270, 220, 290, false, false)
      drawArrow(gg, 220, 290, 250, 290, false)


      drawDot(gg, 140, 200, 5, 5)
      drawArrow(gg, 140, 200, 250, 200, false)

      drawArrow(gg, 220, 200, 220, 220, false, false)
      drawArrow(gg, 220, 220, 250, 220, false)

      drawDot(gg, 150, 270, 5, 5)
      drawArrow(gg, 150, 270, 250, 270)

      drawDot(gg, 130, 100, 5, 5)
      drawArrow(gg, 130, 100, 250, 100, false)

      drawArrow(gg, 170, 270, 170, 420, false, false)

      for(i <- 2 to 7) {
        val m = 20
        val yy = 280
        drawArrow(gg, 170, yy + i*m, 190, yy + i*m)
      }

      drawArrow(gg, 220, 320, 250, 320, false)

      gg setColor Color.BLACK
      gg setFont new Font("Arial", Font.PLAIN, 20)
      gg drawString(name1, 275, 110)
      gg drawString(name2, 275, 130)

      gg drawString ("DC", 192, 325)

      gg setFont new Font("Arial", Font.PLAIN, 12)
      gg drawString("DB", 200, 90)
      gg drawString("/", 200, 105)
      gg drawString("8", 200, 110)

      gg drawString("CB", 200, 190)
      gg drawString("AB", 200, 260)

      for(i <- 2 to 7) {
        val m = 20
        val yy = 275
        gg drawString ("A " + i, 172, yy + i*m)
      }

      gg drawString("D", 255, 100)
      gg drawString("!WR", 255, 200)
      gg drawString("!RD", 255, 225)
      gg drawString("A1", 255, 270)
      gg drawString("A0", 255, 290)
      gg drawString("!CS", 255, 320)

      for(i <- n.indices) {
        gg drawString(n(i), 315, 150 + 200 * i / n.length)
      }
    }

    def drawDot(gg: Graphics2D, x: Int, y: Int, w: Int, h: Int): Unit = {
      gg fillOval(x - w/2, y - h/2, w, h)
    }

    override def paintComponent(g: Graphics): Unit = {
      val gg = g.asInstanceOf[Graphics2D]

      gg setColor Color.WHITE
      gg fillRect(0, 0, getWidth(), getHeight())
      gg scale (0.75, 0.75)

      drawPP(gg, "PPI", "I8255", Array("PC(4)", "PC(0)", "PC(1)", "PC(5)"))

      gg translate (0, 400)
      drawPP(gg, "PT", "I8253", Array("Gate CR0", "Out CR0"))

      gg translate (0, -400)

      gg setColor Color.GRAY
      gg fillRect (10, 50, 100, 300)

      gg fillRect (450, 50, 100, 300)


      gg setColor (Color.RED)
      gg setStroke new BasicStroke(1)
      drawArrow(gg, 130, 100, 130, 500, false, false)
      drawArrow(gg, 140, 200, 140, 600, false, false)
      drawArrow(gg, 150, 270, 150, 670, false, false)

      drawArrow(gg, 110, 100, 130, 100, false, false)
      drawArrow(gg, 110, 200, 140, 200, false, false)
      drawArrow(gg, 110, 270, 150, 270, false, false)

      gg setColor (if (ready) Color.RED else Color.GRAY)
      gg setStroke new BasicStroke(3)
      drawArrow(gg, 450, 200, 350, 200)

      gg setColor (if (pc5) Color.RED else Color.GRAY)
      drawArrowArr(gg, Array(350, 300, 370, 300, 370, 550, 350, 550))
      gg setColor (if (pc1) Color.RED else Color.GRAY)
      drawArrowArr(gg, Array(350, 650, 380, 650, 380, 250, 350, 250))


      gg setColor Color.BLACK
      gg setFont new Font("Arial", Font.PLAIN, 20)
      gg drawString("CPU", 35, 150)
      gg drawString("OBJ", 475, 150)

      gg setFont new Font("Arial", Font.PLAIN, 12)

      gg drawString("Command", 375, 140)
      gg drawString("Ready", 375, 190)

      gg setColor (if (command) Color.RED else Color.GRAY)
      drawArrow(gg, 350, 150, 450, 150)
      gg fillOval (475, 200, 50, 50)

      gg setColor Color.BLACK
      gg drawOval(475, 200, 50, 50)


      //        gg.drawImage(screenIm, 0, 0, null)
      gg.dispose()
    }

    def drawArrowArr(gg: Graphics2D, points: Array[Double]): Unit = {
      for(i <- (0 until points.length - 2) if(i %2 == 0)) {
        drawArrow(gg, points(i), points(i + 1), points(i + 2), points(i + 3), false, i == points.length - 4)
      }
    }

    def drawArrow(gg: Graphics2D, x1: Double, y1: Double, x2: Double, y2: Double,
                  doubleArrow: Boolean = false, drawArrowB: Boolean = true): Unit = {
      if(doubleArrow) {
        drawArrow(gg, x2, y2, x1, y1, false, drawArrowB)
      }

      val dirX = x2 - x1
      val dirY = y2 - y1
      val sc = 0.5;
      val arrD = 10 / Math.hypot(y2 - y1, x2 - x1);

      gg drawLine (x1.toInt, y1.toInt, x2.toInt, y2.toInt);

      if(drawArrowB) {
        val m = Math.sqrt(2) / 2;

        val dx2 = (dirX * m - dirY * m) * sc;
        val dy2 = (dirX * m + dirY * m) * sc;

        val dx3 = (dirX * m + dirY * m) * sc;
        val dy3 = (-dirX * m + dirY * m) * sc;

        gg drawLine (x2.toInt, y2.toInt, ((x1 + dx2 - x2)*arrD + x2).toInt, ((y1 + dy2 - y2)*arrD + y2).toInt);
        gg drawLine (x2.toInt, y2.toInt, ((x1 + dx3 - x2)*arrD + x2).toInt, ((y1 + dy3 - y2)*arrD + y2).toInt);
      }
    }
  }

  def redraw(): Unit = {
    canvas.repaint()
  }
}

object Window {
  def main(args: Array[String]): Unit = {
    val window = new Window(800, 600, "ARCH lab 3")
  }
}
