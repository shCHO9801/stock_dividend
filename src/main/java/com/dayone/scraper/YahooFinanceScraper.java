package com.dayone.scraper;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {

    private static final String STATISTICS_URL =
            "https://finance.yahoo.com/quote/%s/history/?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000;
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);

            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/85.0.4183.121 Safari/537.36")
                    .timeout(10000);
            Document document = connection.get();

            Elements parseDivs = document.select("table.yf-h2urb6.noDl");
            Element tableEle = parseDivs.get(0);  // table 전체

            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException(
                            "Unexpected Month enum value -> " + splits[0]
                    );
                }

                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));
            }
            scrapedResult.setDividends(dividends);
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
        return scrapedResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/85.0.4183.121 Safari/537.36")
                    .timeout(10000).get();
            Element titleEle = document.select("h1").get(1);
            String title = titleEle.text().split("\\(")[0].trim();
            System.out.println(title);
            //.split(" ")[0].trim()

            return new Company(ticker, title);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
