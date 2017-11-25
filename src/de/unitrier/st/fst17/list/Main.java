package de.unitrier.st.fst17.list;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class Main
{
	public static void main(String[] args)
	{
//hehe
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SDRaytracer sdr = new SDRaytracer();

		f.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				boolean redraw = false;
				if (e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					sdr.xAngleFactor--;
					redraw = true;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP)
				{
					sdr.xAngleFactor++;
					redraw = true;
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
				{
					sdr.yAngleFactor--;
					redraw = true;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				{
					sdr.yAngleFactor++;
					redraw = true;
				}
				if (redraw)
				{
					sdr.createScene();
					sdr.renderImage();
					sdr.repaint();
				}
			}
		});
		Container contentPane = f.getContentPane();
		contentPane.setLayout(new BorderLayout());

		long start = System.currentTimeMillis();
		contentPane.add(sdr);
		f.setPreferredSize(new Dimension(sdr.width, sdr.height));
		f.pack();
		f.setVisible(true);
		long end = System.currentTimeMillis();
		long time = end - start;
		System.out.println("time: " + time + " ms");
		System.out.println("nrprocs=" + Runtime.getRuntime().availableProcessors());

	}

}
