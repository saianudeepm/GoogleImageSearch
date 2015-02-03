package com.salome.googleimagesearch.models;

import java.io.Serializable;

/**
 * Created by saianudeepm on 2/1/15.
 */
public class ImageSearchSettings implements Serializable {

    private final static String[] IMAGE_SIZES = {"icon", "small", "medium", "large", "xlarge", "xxlarge", "huge"};
    private final static String[] IMAGE_COLORS = {"black", "blue", "brown", "gray", "green", "orange", "pink", "purple", "red", "white", "yellow"};
    private final static String[] IMAGE_TYPES = {"face", "photo", "clipart", "lineart"};

    public int size = 0;
    public int color = 0;
    public int type = 0;
    public String site = "";
    
    public String getURLParams() {
        
        StringBuilder urlParams = new StringBuilder("");
        
        if (size > 0) {
            urlParams.append("&imgsz=").append(IMAGE_SIZES[size - 1]);
        }
        if (color > 0) {
            urlParams.append("&imgcolor=").append(IMAGE_COLORS[color - 1]);
        }
        if (type > 0) {
            urlParams.append("&imgtype=").append(IMAGE_TYPES[type - 1]);
        }
        if (site.length() > 0) {
            urlParams .append("&as_sitesearch=").append(site);
        }
        return urlParams.toString();
    }
}
