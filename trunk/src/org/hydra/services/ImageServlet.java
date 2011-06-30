package org.hydra.services;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.RandomUtils;
import org.hydra.utils.SessionUtils;

public class ImageServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		int lValue = RandomUtils.nextInt(10);
		int rValue = RandomUtils.nextInt(10);
		String resultStr = "" + lValue + " + " + rValue;
		
		System.out.println("req.getRequestURL(): " + req.getRequestURL());

		BufferedImage image = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_RGB); // 
		Graphics2D graphics2D = image.createGraphics();
		FontMetrics fm = graphics2D.getFontMetrics();
		
		
		int height = fm.getHeight();
		int width = fm.stringWidth(resultStr);
		
		image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB); // 
		graphics2D.dispose();
		graphics2D = image.createGraphics();
		
		graphics2D.setColor(Color.WHITE);
		graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
		graphics2D.setColor(Color.BLACK);
		graphics2D.drawString(resultStr, 0, height);

		graphics2D.dispose();

		response.setContentType("image/jpeg");
		OutputStream outputStream = response.getOutputStream();
		ImageIO.write(image, "jpeg", outputStream);
		outputStream.close();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
