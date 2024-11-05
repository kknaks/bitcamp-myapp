package bitcamp.myapp.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.beans.PropertyEditorSupport;
import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class GlobalControllerAdvice {
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.registerCustomEditor(
        java.util.Date.class,
        new PropertyEditorSupport() {
          @Override
          public void setAsText(String text) throws IllegalArgumentException {
            this.setValue(java.sql.Date.valueOf(text));
          }
        }
    );
  }

  @ExceptionHandler
  public ModelAndView exceptionHandle(Exception e) {
    ModelAndView mv = new ModelAndView();

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    e.printStackTrace(printWriter);

    mv.addObject("exception", stringWriter.toString());
    mv.setViewName("error");
    return mv;
  }
}
