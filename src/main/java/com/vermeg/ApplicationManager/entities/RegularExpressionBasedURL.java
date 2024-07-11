package com.vermeg.ApplicationManager.entities;

import jakarta.persistence.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@DiscriminatorValue("RegularExpressionBasedURL")
public class RegularExpressionBasedURL extends URLBased{
    @Column(name = "regularExpression_")
    private String regularExpression;

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    @Override
    public String commandUrl() throws IOException {
        Document doc = Jsoup.connect(getUrl()).get();
        String input = doc.html();

        Pattern pattern = Pattern.compile(getRegularExpression());
        Matcher matcher = pattern.matcher(input);

        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        matches.sort(String::compareTo);
        return matches.getLast();
    }
}
