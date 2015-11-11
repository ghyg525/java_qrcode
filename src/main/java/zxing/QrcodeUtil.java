package zxing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


/**
 * 二维码生成工具类
 */
public class QrcodeUtil {
	
	/**
	 * 生成二维码
	 * @param filePath
	 * @param outputStream
	 * @return
	 * @throws Exception 
	 */
	public static OutputStream createQrcode(OutputStream outputStream, String content) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 设置QR二维码的纠错级别 (H为最高级别)
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "utf8");	// 编码
		hints.put(EncodeHintType.MARGIN, 2);	// 白边宽度
		MultiFormatWriter writer = new MultiFormatWriter();
		// 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数  
		BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400, hints);
		MatrixToImageWriter.writeToStream(matrix, "jpg", outputStream);	// 生成图片
		return outputStream;
	}
	
	/**
	 * 生成二维码
	 * @param filePath
	 * @param content
	 * @return
	 * @throws Exception 
	 */
	public static File createQrcode(File file, String content) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);	// 容错等级
		hints.put(EncodeHintType.CHARACTER_SET, "utf8");	// 编码
		hints.put(EncodeHintType.MARGIN, 2);	// 白边宽度
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400, hints);	// 指定宽度
		MatrixToImageWriter.writeToFile(matrix, "jpg", file);	// 生成图片
		return file;
	}
	
	/**
	 * 生成二维码, 带logo
	 * @param filePath
	 * @param content
	 * @return
	 * @throws Exception 
	 */
	public static File createQrcodeLogo(File file, File logo, String content) throws Exception {
		file = createQrcode(file, content);
		return addLogo(file, file, logo);
	}
	
	/**
	 * 解析二维码, 部分内容会解析失败
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String parseQrcode(File file) throws Exception {
		BufferedImage image = ImageIO.read(file);
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		Binarizer binarizer = new HybridBinarizer(source);
		BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
		Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8");	// 指定编码
		Result result = new MultiFormatReader().decode(binaryBitmap, hints);
		return result.getText();
	}
	
	/**
	 * 给二维码添加logo
	 * @param file 新图片
	 * @param qrcode
	 * @param logo
	 * @return
	 * @throws Exception
	 */
	public static File addLogo(File file, File qrcode, File logo) throws Exception {
        //读取图片, 并构建绘图对象 
		BufferedImage logoImage = ImageIO.read(logo);  
        BufferedImage qrcodeImage = ImageIO.read(qrcode);  
        Graphics2D g = qrcodeImage.createGraphics();  
        //设置logo的大小, 最大为二维码图片的20%, 因为过大会盖掉二维码 
        int maxWidth = qrcodeImage.getWidth()*2/10;
        int maxHeight = qrcodeImage.getHeight()*2/10;
        int logoWidth = logoImage.getWidth()>maxWidth ? maxWidth : logoImage.getWidth();
        int logoHeight = logoImage.getHeight()>maxHeight ? maxHeight : logoImage.getHeight();  
        // 计算图片放置位置, 中心
        int x = (qrcodeImage.getWidth() - logoWidth) / 2; 
        int y = (qrcodeImage.getHeight() - logoHeight) / 2;
        //开始绘制图片  
        g.drawImage(logoImage, x, y, logoWidth, logoHeight, null);  
        g.drawRoundRect(x, y, logoWidth, logoHeight, 15, 15);  
        g.setStroke(new BasicStroke(2));  
        g.setColor(Color.WHITE);  
        g.drawRect(x, y, logoWidth, logoHeight);  
        g.dispose();  
        logoImage.flush();  
        qrcodeImage.flush();  
        // 生成新图
        ImageIO.write(qrcodeImage, "jpg", file);  
		return file;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		File file = new File("c:/test.jpg");
		QrcodeUtil.createQrcodeLogo(file, file, "中文");
		System.out.println(QrcodeUtil.parseQrcode(file));
	}
	
}
