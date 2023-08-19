package com.example.demo.type.file;

import com.example.demo.type.EnumReverseLookupable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageFileSizeType implements EnumReverseLookupable {

 SMALL(1, "small")
  , MEDIUM(2, "medium")
  , LARGE(3, "large")
  , EXTRA_LARGE(4, "extraLarge");
	
  private final int code;
  private final String property;

  public static ImageFileSizeType getByProperty(String value) {
      return EnumReverseLookupable.getByCondition(ImageFileSizeType.class, (ImageFileSizeType e) -> e.property.equals(value));      
  }
}
