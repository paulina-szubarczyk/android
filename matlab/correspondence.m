function correspondence()
   clc; clear all
   
    color = imread('camera1432407866.jpg');
    term = imread('thermapp1432407857.jpg');
    
    color = rotColor90(color, -1);
    term = rotColor90(term, -1);

    term = flipdim(term, 2);
    
    [H W C] = size(color);
    
    frac = 0.30;
    horizontalTranslate = -7;
    verticalTranslate = +45;
    left = round(W * frac) + horizontalTranslate;
    right = W - left + 2 * horizontalTranslate;
    bottom = round(H * frac) + verticalTranslate;
    top = H - bottom + 2 * verticalTranslate;
    
    
    color = color(bottom:top, left:right, :);
    
    color = imresize(color, [size(term, 1) size(term, 2)]);
    
    size(color)
    x1 = 30;
    x2 = 280;
    y1 = 50;
    y2 = 330;
    
    figure(1)
    subplot(2, 1, 1);
    image(color);
    grid on
    subplot(2, 1, 2);
    image(term);
    grid on
end

function rotated = rotColor90(img, k)
    [H W C] = size(img);
    
    rotated = zeros(W, H, C, 'uint8');
    for i = 1:C
        rotated(:, :, i) = rot90(img(:, :, i), k);
    end    
end