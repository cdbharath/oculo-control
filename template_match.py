import cv2
import numpy as np

def match(img_rgb):
	
	img_gray = cv2.cvtColor(img_rgb, cv2.COLOR_BGR2GRAY)

	left = cv2.imread('kohli.jpeg', 0)
	#template = cv2.resize(template,(70,70))
	w, h = left.shape[::-1]
	right = cv2.imread('kohli.jpeg', 0)
	up = cv2.imread('kohli.jpeg', 0)
	down = cv2.imread('kohli.jpeg', 0)
	blink = cv2.imread('kohli.jpeg', 0)
	
	res_left = cv2.matchTemplate(img_gray,left,cv2.TM_CCOEFF_NORMED) #TM_SQDIFF-Best Match///TM_CCOEFF_NORMED-Threshold Match
	res_right = cv2.matchTemplate(img_gray,right,cv2.TM_CCOEFF_NORMED) #TM_SQDIFF-Best Match///TM_CCOEFF_NORMED-Threshold Match
	res_up = cv2.matchTemplate(img_gray,up,cv2.TM_CCOEFF_NORMED) #TM_SQDIFF-Best Match///TM_CCOEFF_NORMED-Threshold Match
	res_down = cv2.matchTemplate(img_gray,down,cv2.TM_CCOEFF_NORMED) #TM_SQDIFF-Best Match///TM_CCOEFF_NORMED-Threshold Match
	res_blink = cv2.matchTemplate(img_gray,blink,cv2.TM_CCOEFF_NORMED) #TM_SQDIFF-Best Match///TM_CCOEFF_NORMED-Threshold Match

	left_threshold = 0.8
	right_threshold = 0.8
	up_threshold = 0.8
	down_threshold = 0.8
	blink_threshold = 0.8

	loc_left = np.where( res_left >= left_threshold)
	loc_right = np.where( res_right >= right_threshold)
	loc_up = np.where( res_up >= up_threshold)
	loc_down = np.where( res_down >= down_threshold)
	loc_blink = np.where( res_blink >= blink_threshold)

	for pt in zip(*loc[::-1]):
	    cv2.rectangle(img_rgb, pt, (pt[0] + w, pt[1] + h), (0,0,255), 1)
	for pt in zip(*loc[::-1]):
	    cv2.rectangle(img_rgb, pt, (pt[0] + w, pt[1] + h), (255,0,0), 1)
	for pt in zip(*loc[::-1]):
	    cv2.rectangle(img_rgb, pt, (pt[0] + w, pt[1] + h), (0,255,0), 1)
	for pt in zip(*loc[::-1]):
	    cv2.rectangle(img_rgb, pt, (pt[0] + w, pt[1] + h), (0,255,255), 1)
	for pt in zip(*loc[::-1]):
	    cv2.rectangle(img_rgb, pt, (pt[0] + w, pt[1] + h), (255,255,0), 1)

	return img_rgb

if __name__ == "__main__":
    match(cv2.imread('crowd.jpg'))
