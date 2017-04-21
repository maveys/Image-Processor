import java.awt.Color;
import java.util.ArrayList;
public class ImageProcessor {

	private Picture photo;
	private int h, originalWidth;
	private Color[][] RGBColors;
	private int[][]	matrix;
	
	/**
	 * @author Chris Bui & Michael Bonpua
	 * @param imageFile holds the name of the image that will be manipulated
	 */
	public ImageProcessor(String imageFile){
		
		//get image info
		photo = new Picture(imageFile);
		h = photo.height();
		originalWidth = photo.width();
		RGBColors = new Color[h][originalWidth];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < originalWidth; j++) {
				RGBColors[i][j] = photo.get(j, i);
			}
		}
		matrix = new int[h][originalWidth];
		int i = 0, j = 0;
		//Given a picture with width W and height H, let M the matrix that represents the image. Now,
		//given a pixel M[i, j], if 0 < i < H
		for (i = 0; i < h; i++) {
			for(j = 0; j < originalWidth; j++){
				//get Y importance value for row
				if(i == 0){//Y Importance(M[i, j]) = Dist(M[H - 1, j, ]M[i + 1, j])
					matrix[i][j] = dist(RGBColors[h-1][j],RGBColors[i+1][j]);
				}
				else if(i == h - 1){//Y Importance(M[i, j]) = Dist(M[i - 1, j, ]M[0, j])
					matrix[i][j] = dist(RGBColors[i-1][j],RGBColors[0][j]);
				}
				else {//Y Importance(M[i, j]) = Dist(M[i - 1, j], M[i + 1, j])
					matrix[i][j] = dist(RGBColors[i-1][j],RGBColors[i+1][j]);
				}
				
				//get X importance value for column and adds to Y importance
				//Importance(M[i, j]) = XImportance(M[i, j]) + Y Importance(M[i, j])
				if(j == 0){//X Importance(M[i, j]) = Dist(M[i, W - 1, ]M[i, j + 1])
					matrix[i][j] += dist(RGBColors[i][originalWidth-1],RGBColors[i][j+1]);
				}
				else if(j == originalWidth - 1){//X Importance(M[i, j]) = Dist(M[i, 0]M[i, j - 1])
					matrix[i][j] += dist(RGBColors[i][0],RGBColors[i][j-1]);
				}
				else {//X Importance(M[i, j]) = Dist(M[i, j - 1], M[i, j + 1])
					matrix[i][j] += dist(RGBColors[i][j-1],RGBColors[i][j+1]);
				}
			}
		}//end for
		
	}
	
	/**
	 * Compute Min-Cost vertical cut of importanceMatrix
	 * For every i, remove the pixel M[i, yi] from the image. Now the width of the image is W - 1.
	 * To reduce the width from W to W - k, repeat the above procedure k times.
	 * Must use the static method minCostVC to compute the vertical cut.
	 * @param x reduced width we will make the image
	 * @return a new Picture whose width is [x נW]
	 */
	public Picture reduceWidth(double x){
		Picture picture = new Picture(photo);
		int repeat = (int) (originalWidth - Math.ceil(x * originalWidth));
		
		for (int r = 0; r < repeat; r++) {
			int height = picture.height();
			int width = picture.width();
			
			RGBColors = new Color[h][picture.width()];
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < picture.width(); j++) {
					RGBColors[i][j] = picture.get(j, i);
				}
			}
			matrix = getMatrix(picture);
			
			ArrayList<Integer> pixelsToRemove = DynamicProgramming.minCostVC(matrix);
		
			Picture newPicture = new Picture(width - 1, height);
			int column = 0;
			
			for (int i = 0; i < h; i++) {
				column = 0;
				int row = pixelsToRemove.get(2 * i);
				int col = pixelsToRemove.get((2 * i) + 1);
				//System.out.println(row + " " + col);
				for (int j = 0; j < originalWidth; j++) {
					if (column >= width - 1) break;
					
//					if (i == row && j == col) {
//					}
//					else {
//						newPicture.set(column++, i, RGBColors[i][j]);
//					}
					
					if (!(i == row && j == col)) {
						newPicture.set(column++, i, RGBColors[i][j]);
					}
					/* 
					 if (i != row && j != col) {
						newPicture.set(column++, i, RGBColors[i][j]);
					}
					 */
				}
			}
			//if(r % 50 == 0) picture.save("modified_images/20_" + r + ".jpg");
			picture = newPicture;
		}
		
		return picture;
	}
	
	/**
	 * Dist(p, q) = (r1 - r2)^2 + (g1 - g2)^2 + (b1 - b2)^2
	 * @return
	 */
	private int dist(Color p, Color q){
		
		double red =  Math.pow(p.getRed() - q.getRed(), 2);
		double green =  Math.pow(p.getGreen() - q.getGreen(), 2);
		double blue =  Math.pow(p.getBlue() - q.getBlue(), 2);
		double distance = red + green + blue;
		
		return (int) distance;
	}
	
	
	private int[][] getMatrix(Picture image) {
		int height = image.height();
		int width = image.width();
		int[][] importanceMatrix = new int[height][width];
	
		int i = 0, j = 0;
		//Given a picture with width W and height H, let M the matrix that represents the image. Now,
		//given a pixel M[i, j], if 0 < i < H
		for (i = 0; i < height; i++) {
			for(j = 0; j < width; j++){
				//get Y importance value for row
				if(i == 0){//Y Importance(M[i, j]) = Dist(M[H - 1, j, ]M[i + 1, j])
					importanceMatrix[i][j] = dist(RGBColors[height-1][j],RGBColors[i+1][j]);
				}
				else if(i == height - 1){//Y Importance(M[i, j]) = Dist(M[i - 1, j, ]M[0, j])
					importanceMatrix[i][j] = dist(RGBColors[i-1][j],RGBColors[0][j]);
				}
				else {//Y Importance(M[i, j]) = Dist(M[i - 1, j], M[i + 1, j])
					importanceMatrix[i][j] = dist(RGBColors[i-1][j],RGBColors[i+1][j]);
				}
				
				//get X importance value for column and adds to Y importance
				//Importance(M[i, j]) = XImportance(M[i, j]) + Y Importance(M[i, j])
				if(j == 0){//X Importance(M[i, j]) = Dist(M[i, W - 1, ]M[i, j + 1])
					importanceMatrix[i][j] = importanceMatrix[i][j] + dist(RGBColors[i][width-1],RGBColors[i][j+1]);
				}
				else if(j == width - 1){//X Importance(M[i, j]) = Dist(M[i, 0]M[i, j - 1])
					importanceMatrix[i][j] = importanceMatrix[i][j] + dist(RGBColors[i][0],RGBColors[i][j-1]);
				}
				else {//X Importance(M[i, j]) = Dist(M[i, j - 1], M[i, j + 1])
					importanceMatrix[i][j] = importanceMatrix[i][j] + dist(RGBColors[i][j-1],RGBColors[i][j+1]);
				}
			}
		}
		
		return importanceMatrix;
	}
}
