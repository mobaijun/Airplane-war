package com.mobai.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

/**
 * 飞机游戏的主窗口
 */
public class MyGameFrame extends Frame {

    Image planeImg = GameUtil.getImage("images/plane.png");
    Image bg = GameUtil.getImage("images/bg.jpg");

    Plane plane = new Plane(planeImg, 250, 250);
    Shell[] shells = new Shell[20];

    Explode bao;
    Date startTime = new Date();
    Date endTime;
    /**
     * 游戏持续的时间
     */
    int period;

    /**
     * // 自动被调用。  g相当于一只画笔
     *
     * @param g the specified Graphics window
     */
    @Override
    public void paint(Graphics g) {
        Color c = g.getColor();
        g.drawImage(bg, 0, 0, null);
// 画飞机
        plane.drawSelf(g);

        // 画出所有的炮弹
        for (Shell shell : shells) {
            shell.draw(g);

            // 飞机和炮弹的碰撞检测！！！
            boolean peng = shell.getRect().intersects(plane.getRect());
            if (peng) {
                plane.live = false;
                if (bao == null) {
                    bao = new Explode(plane.x, plane.y);

                    endTime = new Date();
                    period = (int) ((endTime.getTime() - startTime.getTime()) / 1000);
                }
                bao.draw(g);
            }

            // 计时功能，给出提示
            if (!plane.live) {
                g.setColor(Color.red);
                Font f = new Font("宋体", Font.BOLD, 50);
                g.setFont(f);
                g.drawString("时间：" + period + "秒", (int) plane.x, (int) plane.y);
            }

        }

        g.setColor(c);
    }


    /**
     * 帮助我们反复的重画窗口！
     */
    class PaintThread extends Thread {
        @Override
        public void run() {
            while (true) {
                // 重画
                repaint();
                try {
                    // 1s=1000ms
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 定义键盘监听的内部类
     */
    class KeyMonitor extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            plane.addDirection(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            plane.minusDirection(e);
        }


    }


    /**
     * 初始化窗口
     */
    public void launchFrame() {
        this.setTitle("框架师_墨白");
        this.setVisible(true);
        this.setSize(Constant.GAME_WIDTH, Constant.GAME_HEIGHT);
        this.setLocation(300, 300);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        // 启动重画窗口的线程
        new PaintThread().start();
        // 给窗口增加键盘的监听
        addKeyListener(new KeyMonitor());


        // 初始化50个炮弹
        for (int i = 0; i < shells.length; i++) {
            shells[i] = new Shell();
        }

    }

    public static void main(String[] args) {
        MyGameFrame f = new MyGameFrame();
        f.launchFrame();
    }

    private Image offScreenImage = null;

    public void update(Graphics g) {
        if (offScreenImage == null) {
            // 这是游戏窗口的宽度和高度
            offScreenImage = this.createImage(Constant.GAME_WIDTH, Constant.GAME_HEIGHT);
        }

        Graphics gOff = offScreenImage.getGraphics();
        paint(gOff);
        g.drawImage(offScreenImage, 0, 0, null);
    }
}
