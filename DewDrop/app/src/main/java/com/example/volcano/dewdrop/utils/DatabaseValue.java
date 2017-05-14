package com.example.volcano.dewdrop.utils;

import java.io.File;
import java.util.Map;

/**
 * Created by volcano on 02/05/17.
 */

public interface DatabaseValue {

    Map<String, Object> toMap();

    String getPrimaryKey();

    File[] getImages();

    boolean hasImages();

}
