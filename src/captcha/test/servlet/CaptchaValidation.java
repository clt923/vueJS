package captcha.test.servlet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import jdk.nashorn.internal.parser.JSONParser;

@WebServlet("/validation")
public class CaptchaValidation extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String view = "/captcha-validation.jsp";

        String gRecaptchaResponse = req.getParameter("g-recaptcha-response");
        JSONObject json = getJSONResponse(gRecaptchaResponse);

        boolean isSuccess = (boolean) json.get("success");
        req.setAttribute("gRecaptchaResponse", gRecaptchaResponse);
        req.setAttribute("isSuccess", isSuccess);
        req.setAttribute("json", json.toString());
        req.getRequestDispatcher(view).forward(req, resp);
    }

    private JSONObject getJSONResponse(String gRecaptchaResponse) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        String secretKey = "6LffpnoUAAAAAO8yDjVzMTGtvflsZW1K7g7c****";

        String response = getResponse(url, secretKey, gRecaptchaResponse);
        JSONObject json = getJSONObject(response);

        return json;
    }

    private JSONObject getJSONObject(String jsonString) {
        JSONObject json = new JSONObject();

        try {
            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(jsonString);
            System.out.println("json: " + json.toJSONString());

        } catch (Exception e) {

        }

        return json;
    }

    private String getResponse(String url, String secretKey, String gRecaptchaResponse) {
        String response = "";

        try {
            URL urlObject = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) urlObject.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String param = "secret=" + secretKey + "&response=" + gRecaptchaResponse;

            DataOutputStream stream = new DataOutputStream(connection.getOutputStream());
            stream.writeBytes(param);
            stream.flush();
            stream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                response += inputLine;
            }
            reader.close();

        } catch (Exception e) {

        }

        return response;
    }
}