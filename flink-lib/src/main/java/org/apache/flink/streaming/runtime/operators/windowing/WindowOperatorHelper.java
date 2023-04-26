package org.apache.flink.streaming.runtime.operators.windowing;

import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.apache.flink.streaming.api.operators.SimpleOperatorFactory;
import org.apache.flink.streaming.api.operators.SimpleUdfStreamOperatorFactory;
import org.apache.flink.streaming.api.transformations.OneInputTransformation;
import org.apache.flink.util.OutputTag;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WindowOperatorHelper {

  public static Object getField(Object obj, Class declaringClazz, String field) {
    try {
      Field f = declaringClazz.getDeclaredField(field);
      f.setAccessible(true);
      return f.get(obj);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Couldn't get " + field + " from " + obj, e);
    }
  }

  public static Object getField(Object obj, String field) {
    return getField(obj, obj.getClass(), field);
  }

  public static void setField(Object obj, Class declaringClazz, String field, Object value) {
    try {
      Field f = declaringClazz.getDeclaredField(field);
      f.setAccessible(true);
      f.set(obj, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Couldn't set " + field + " from " + obj, e);
    }
  }

  public static void setField(Object obj, String field, Object value) {
    setField(obj, obj.getClass(), field, value);
  }

  public static void setPrivateFinalField(Object o, Field f, Object v)
      throws NoSuchFieldException, IllegalAccessException {
    // circumvent private modifier of the field
    f.setAccessible(true);
    // circumvent final modifier of the field
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
    // set new value for the field
    f.set(o, v);
  }

  public static void replaceOperator(OneInputTransformation transformation,
      OneInputStreamOperator operator) {
    try {
      Object operatorFactory = getField(transformation, "operatorFactory");
      setPrivateFinalField(operatorFactory,
          SimpleUdfStreamOperatorFactory.class.getDeclaredField("operator"),
          operator);
      setPrivateFinalField(operatorFactory,
          SimpleOperatorFactory.class.getDeclaredField("operator"),
          operator);
    } catch (Exception e) {
      throw new RuntimeException("Couldn't replace operator of " + transformation, e);
    }
  }

  public static void enrichWindowOperator(OneInputTransformation transformation,
      OutputTag mappedEventOutputTag) {
    replaceOperator(
        transformation,
        MapWithStateWindowOperator.from(
            (WindowOperator) transformation.getOperator(),
            mappedEventOutputTag
        )
    );
  }

  public static <IN, ACC, MAPPED> void enrichWindowOperator(OneInputTransformation transformation,
      MapWithStateFunction<IN, ACC, MAPPED> mapWithStateFunction,
      OutputTag mappedEventOutputTag) {
    replaceOperator(
        transformation,
        MapWithStateWindowOperator.from(
            (WindowOperator) transformation.getOperator(),
            mapWithStateFunction,
            mappedEventOutputTag
        )
    );
  }
}
