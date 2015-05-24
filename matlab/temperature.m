function temperature()
       
%     data = dlmread('thermapp1432229544.txt', '\t');
    data = dlmread('thermapp1432230291.txt', '\t');
%     data = dlmread('thermapp1432230292.txt', '\t');
    
    width = 384;
    height = 288;
    
    data = reshape(data, width, height)';
%     data = data / 100;307
    
    maxTemp = max(data(:));
    minTemp = min(data(:));
    data = data - minTemp;
    maxTemp = maxTemp - minTemp;
    
%     data = data * 64 / maxTemp;
    
    max(data(:))
    
    
    figure(1)
%     img = image(data);
    image(data, 'CDataMapping','scaled');
    colorbar
    
    cmap = colormap;
    colorspace = size(cmap, 1);
    
    colorscale = round(interp1(linspace(0, maxTemp, colorspace), 1:colorspace, data));
    
    mappedData = reshape(cmap(colorscale, :), [size(colorscale) 3]);
    figure(2)
    image(mappedData)
    colorbar
    
    
end