package net.pms.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;

public class WebTest {
//    @Test
    public void testWeb() throws Exception {
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        
        HtmlPage page = webClient.getPage(
                "http://localhost:9001/" );

        // Homepage
        HtmlAnchor link = page.getAnchors().get(0);
        page = link.click();
        
        // First page
        link = page.getAnchorByText("Media");
        page = link.click();
        
        // Content page
        link = page.getAnchorByText("Untitled");
        page = link.click();
        
        // Play page
        DomElement element = page.getElementById("ImageContainer");
        assertTrue(element != null);
        
        // Search start
        HtmlForm form = (HtmlForm) page.getElementById("SearchForm");
        HtmlTextInput textField = form.getInputByName("str");
        textField.type("un");
        HtmlSubmitInput button = (HtmlSubmitInput) page.getElementById("SearchSubmit");
        page = button.click();
        
        // Search results
        HtmlUnorderedList results = (HtmlUnorderedList) page.getElementById("Media");
        assertTrue(results != null);
        assertEquals(1, results.getChildElementCount());

        webClient.close();
    }
}
