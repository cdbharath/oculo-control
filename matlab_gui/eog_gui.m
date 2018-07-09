%
%         GUI for EOG Signal classification  
%

raw_left = imread('left.png');
raw_right = imread('right.png');
raw_center = imread('center.png');
background = imread('white.png');

left = background;
right = background;
center = background;

size(raw_left)
size(raw_right)
size(raw_center)
size(background)
size(left(20:99,17:266,:))

left(20:99,17:266,:) = raw_left;
right(20:99,17:266,:) = raw_right;
center(20:99,17:266,:) = raw_center;

status = "left" ; 

position = [100 120];
text = ['left'];

while 1
if status == "left"
final = insertText(left,[100 120],['left'],'FontSize',32,'BoxOpacity',0);   
imshow(final);
end
if status == "right"
final = insertText(right,[90 120],['right'],'FontSize',32,'BoxOpacity',0);
imshow(final);
end
if status == "center" 
final = insertText(center,[80 120],['center'],'FontSize',32,'BoxOpacity',0);
imshow(final);
end
pause(0.05);
end