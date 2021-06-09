package com.example.readxml;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.database.dbXML;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // -------- Khởi tạo database
    private SQLiteDatabase db;

    // -------- Tên của thẻ cho data
    private static final String TAG_XML_PULL_PARSER = "XML_PULL_PARSER";

    // -------- Thông báo cho trình xử lý hoạt động để hiển thị kết quả với cú pháp xml
    private static final int MESSAGE_SHOW_XML_PARSE_RESULT = 1;

    // -------- Key data thông báo
    private static final String KEY_XML_PARSE_RESULT = "KEY_XML_PARSE_RESULT";

    // -------- Phân tích cú pháp xml sử dụng button XmlPullParser
    private Button parseXmlUsePullButton = null;

    // -------- Hiển thị chế độ xem
    private TextView showXmlParseResultTextView = null;

    // -------- Đợi xử lý cú pháp xml
    private Handler showParseResultHandler = null;

    // OkHttpClient để đọc xml từ url
    private OkHttpClient okHttpClient = null;

    // đường dẫn url.
    private String xmlFileUrl;

    // ArrayList lưu img.
    private ArrayList<String> arrayListImg;

    // Edittext
    private EditText edtURL;

    private int i = 0;
    private String url, phanTram, ten, ngayThang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo tất cả bên UI.
        initControls();

        // Khi click button này để bắt đầu phân tích cú pháp url xml bằng XmlPullParser.
        parseXmlUsePullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edtURL.getText().toString().trim())) {
                    xmlFileUrl = edtURL.getText().toString().trim();
                }

                if(URLUtil.isHttpUrl(xmlFileUrl) || URLUtil.isHttpsUrl(xmlFileUrl)) {

                    // -------- Tạo yêu cầu OkHttpClient.
                    Request.Builder builder = new Request.Builder();

                    // -------- Set xml của url.
                    builder  = builder.url(xmlFileUrl);

                    // -------- Xây dựng yêu cầu HTTP.
                    Request request = builder.build();

                    // -------- Tạo okHttpClient.
                    Call call = okHttpClient.newCall(request);

                    // -------- Thực hiện yêu cầu get xml không đồng bộ trong 1 chuỗi tự động.
                    call.enqueue(new Callback() {

                        // -------- Nếu HTTP nhận về mà không thành công.
                        @Override
                        public void onFailure(Call call, IOException e) {
                            sendXmlParseResultToActivityHandler(e.getMessage());
                        }

                        // -------- Nếu HTTP nhận về thành công.
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            // -------- Nếu server phản hồi về thành công.
                            if(response.isSuccessful())
                            {
                                // -------- Nhận chuỗi xml trả về.
                                String resultXml = response.body().string();

                                // -------- Phân tích xml.
                                String xmlParseResult = parseXmlUsePullParser(resultXml);

                                // -------- Gửi tin nhắn tới function sendXmlParseResultToActivityHandler để hiển thị kết quả được phân tích cú pháp xml.
                                sendXmlParseResultToActivityHandler(xmlParseResult);
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendXmlParseResultToActivityHandler(String xmlParseResult)
    {
        // ---- Tạo đối tượng Message.
        Message msg = new Message();
        msg.what = MESSAGE_SHOW_XML_PARSE_RESULT;

        // ---- Thông báo error.
        Bundle bundle = new Bundle();
        bundle.putString(KEY_XML_PARSE_RESULT, xmlParseResult);
        msg.setData(bundle);

        // Gửi Message đến showParseResultHandler.
        showParseResultHandler.sendMessage(msg);
    }

    //  Phân tích chuỗi xml và sử dụng chuỗi đã phân tích cú pháp XmlPullParser.Return.
    private String parseXmlUsePullParser(String xmlString)
    {
        StringBuffer retBuf = new StringBuffer();
        try {
            // ------ Tạo XmlPullParserFactory để lấy xml về.
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();

            // ------ Tạo XmlPullParser.
            XmlPullParser xmlPullParser = parserFactory.newPullParser();

            // ------ Tạo new StringReader.
            StringReader xmlStringReader = new StringReader(xmlString);

            // ------ Đặt StringReader làm đầu vào XmlPullParser.
            xmlPullParser.setInput(xmlStringReader);

            // ------ Get các sự kiện trong lúc phân tích.
            int eventType = xmlPullParser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {

                // Get tên phần tử xml.
                String nodeName = xmlPullParser.getName();
                if (!TextUtils.isEmpty(nodeName)) {
                    if (eventType == XmlPullParser.START_TAG) {
                        Log.e(TAG_XML_PULL_PARSER, "Start element " + nodeName);

                        if ("loc".equalsIgnoreCase(nodeName) || "lastmod".equalsIgnoreCase(nodeName) || "changefreq".equalsIgnoreCase(nodeName) || "priority".equalsIgnoreCase(nodeName) || "image:loc".equalsIgnoreCase(nodeName)) {
                            retBuf.append(nodeName);

                            // ------- Get các text từ xml.
                            String value = String.valueOf(xmlPullParser.nextText());
                            Log.e(TAG_XML_PULL_PARSER, "element text : " + value);

                            if (nodeName.equals("image:loc")) {
                                i++;
                            }
                            if (nodeName.equals("loc")) {
                                url = value;
                            }
                            if (nodeName.equals("lastmod")) {
                                ngayThang = value;
                            }
                            if (nodeName.equals("changefreq")) {
                                ten = value;
                            }
                            if (nodeName.equals("priority")) {
                                phanTram = value;
                            }
                            retBuf.append(" : ");
                            retBuf.append(value);
                            Log.e("value", value);
                            retBuf.append("\r\n\r\n");
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        Log.d(TAG_XML_PULL_PARSER, "End element " + nodeName);
                        if("url".equalsIgnoreCase(nodeName))
                        {
                            Log.e("đây là: ", url);
                            Log.e("đây là: ", ngayThang);
                            Log.e("đây là: ", ten);
                            Log.e("đây là: ", phanTram);
                            retBuf.append("Tổng số img là: "+ i + "\n");
                            retBuf.append("----------------------------------------------\r\n\r\n");
                            i = 0;
                        }
                    }
                }
                eventType = xmlPullParser.next();
            }
        }catch(XmlPullParserException ex)
        {
            // --------- Nếu có lỗi thêm thông báo.
            retBuf.append(ex.getMessage());
        }finally {
            return retBuf.toString();
        }
    }

    // -------- Khởi tạo.
    private void initControls()
    {
        edtURL = (EditText)findViewById(R.id.url);
        if(parseXmlUsePullButton == null)
        {
            parseXmlUsePullButton = (Button)findViewById(R.id.xml_pull_parser_parse_button);
        }
        if(showXmlParseResultTextView == null)
        {
            showXmlParseResultTextView = (TextView)findViewById(R.id.xml_parse_result_text_view);
        }
        if(showParseResultHandler == null)
        {
            // Đợi hoạt động.
            showParseResultHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    // ---------- Thông báo hiển thị kết quả phân tích cú pháp.
                    if(msg.what == MESSAGE_SHOW_XML_PARSE_RESULT)
                    {
                        // --------- Get dữ liệu.
                        Bundle bundle = msg.getData();

                        // --------- Get kết quả phân tích cú pháp xml.
                        String xmlParseResult = bundle.getString(KEY_XML_PARSE_RESULT);

                        // --------- Show kết quả lên textview.
                        showXmlParseResultTextView.setText(xmlParseResult);
                    }
                }
            };
        }
        if(okHttpClient == null)
        {
            okHttpClient = new OkHttpClient();
        }
    }

}