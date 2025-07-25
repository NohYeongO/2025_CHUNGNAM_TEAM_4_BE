package com.chungnam.eco.admin.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.stereotype.Service;

/**
 * 문장 간 의미적 유사도를 형태소 기반 TF-IDF 방식으로 계산하는 서비스 클래스입니다. KOMORAN 형태소 분석기를 이용해 각 문장의 의미 단어를 추출하고, TF-IDF 벡터화 후 코사인 유사도를
 * 계산합니다.
 */
@Service
public class SentenceSimilarityService {

    private final Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

    /**
     * 주어진 문장에서 형태소를 추출합니다. 품사 제한 없이 모든 형태소를 수집합니다.
     *
     * @param sentence 분석할 문장 (null 또는 빈 문자열일 수 있음)
     * @return 형태소 리스트
     */
    private List<String> extractMorphs(String sentence) {
        if (sentence == null || sentence.trim().isEmpty()) {
            return List.of();
        }
        return komoran.analyze(sentence.trim())
                .getTokenList().stream()
                .map(Token::getMorph)
                .collect(Collectors.toList());
    }

    /**
     * 단어 리스트로부터 TF(Term Frequency)를 계산합니다.
     *
     * @param words 형태소 리스트
     * @return 단어별 TF 값 맵 (0~1 사이 값)
     */
    private Map<String, Double> computeTF(List<String> words) {
        if (words.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Long> freq = words.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        double total = words.size();
        return freq.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().doubleValue() / total
                ));
    }

    /**
     * 문서 집합으로부터 IDF(Inverse Document Frequency)를 계산합니다. 스무딩 적용: log((N + 1) / (df + 1)) + 1
     *
     * @param documents 각 문서를 구성하는 형태소 리스트들의 집합
     * @return 단어별 IDF 값 맵
     */
    private Map<String, Double> computeIDF(List<List<String>> documents) {
        int totalDocs = documents.size();

        Map<String, Long> docFreq = documents.stream()
                .flatMap(doc -> new HashSet<>(doc).stream())
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        return docFreq.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.log((totalDocs + 1.0) / (e.getValue() + 1.0)) + 1.0
                ));
    }

    /**
     * 주어진 단어 리스트를 기반으로 TF-IDF 벡터를 생성합니다.
     *
     * @param words 해당 문장의 형태소 리스트
     * @param idf   전체 문서 기준 IDF 맵
     * @param vocab 전체 문서에서 등장한 단어 집합
     * @return TF-IDF 벡터 (단어별 가중치)
     */
    private Map<String, Double> computeTFIDF(List<String> words, Map<String, Double> idf, Set<String> vocab) {
        Map<String, Double> tf = computeTF(words);
        Map<String, Double> tfidf = new HashMap<>();

        for (String word : vocab) {
            double tfVal = tf.getOrDefault(word, 0.0);
            double idfVal = idf.getOrDefault(word, 0.0);
            tfidf.put(word, tfVal * idfVal);
        }

        return tfidf;
    }

    /**
     * 두 TF-IDF 벡터 간의 코사인 유사도를 계산합니다.
     *
     * @param v1 문장1의 벡터
     * @param v2 문장2의 벡터
     * @return 0.0 ~ 1.0 사이의 유사도 점수
     */
    private double cosineSimilarity(Map<String, Double> v1, Map<String, Double> v2) {
        double dot = 0.0, normA = 0.0, normB = 0.0;

        for (String key : v1.keySet()) {
            double a = v1.get(key);
            double b = v2.getOrDefault(key, 0.0);
            dot += a * b;
            normA += a * a;
        }

        normB = v2.values().stream().mapToDouble(v -> v * v).sum();

        return (normA != 0 && normB != 0) ? dot / (Math.sqrt(normA) * Math.sqrt(normB)) : 0.0;
    }

    /**
     * 두 문장을 형태소 분석 기반 TF-IDF로 벡터화한 후 코사인 유사도를 계산하여 의미적 유사도를 측정합니다.
     *
     * @param sentence1 문장1
     * @param sentence2 문장2
     * @return 0.0 ~ 1.0 사이의 유사도 점수 (1에 가까울수록 유사)
     */
    public double checkSimilarity(String sentence1, String sentence2) {
        List<String> keywords1 = extractMorphs(sentence1);
        List<String> keywords2 = extractMorphs(sentence2);

        if (keywords1.isEmpty() || keywords2.isEmpty()) {
            return 0.0;
        }

        Set<String> vocab = new HashSet<>();
        vocab.addAll(keywords1);
        vocab.addAll(keywords2);

        Map<String, Double> idf = computeIDF(List.of(keywords1, keywords2));
        Map<String, Double> tfidf1 = computeTFIDF(keywords1, idf, vocab);
        Map<String, Double> tfidf2 = computeTFIDF(keywords2, idf, vocab);

        return cosineSimilarity(tfidf1, tfidf2);
    }
}
