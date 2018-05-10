package br.com.qisi.skeleton.integration;

import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.Charset;

public interface Utility {

  static String fileContent(final String filePath) throws IOException {
    return Resources.toString(Resources.getResource(filePath), Charset.defaultCharset());
  }
}