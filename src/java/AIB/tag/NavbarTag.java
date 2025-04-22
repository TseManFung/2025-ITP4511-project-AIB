/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AIB.tag;

/**
 *
 * @author andyt
 */
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class NavbarTag extends SimpleTagSupport {
    
    @Override
    public void doTag() throws JspException, IOException {
        try{
        JspWriter out = getJspContext().getOut();
        out.println("ABC");}
        catch(IOException e){
        System.out.println(e);
        }
    }
}
