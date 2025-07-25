package com.chungnam.eco.admin.service;

import com.chungnam.eco.admin.service.dto.AIMissionListResponseDto;
import com.chungnam.eco.admin.service.dto.AIMissionListResponseDto.AIMissionDto;
import com.chungnam.eco.common.exception.AICreationExceptions;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.mission.domain.MissionCategory;
import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.repository.MissionJPARepository;
import com.chungnam.eco.mission.service.dto.MissionDto;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AIMissionService {
    private final OpenAIClient openAIClient;
    private final MissionJPARepository missionJPARepository;
    private final SentenceSimilarityService sentenceSimilarityService;

    /**
     * 생성할 미션의 프롬프트를 셋팅
     *
     * @param missionCategory 생성할 미션 카테고리
     * @param missionType     생성할 미션 타입
     * @param size            생성할 미션의 갯수
     * @return
     */

    private String setMissionPrompt(MissionCategory missionCategory, MissionType missionType, int size) {
        return """
                당신은 시민들의 친환경 실천을 유도하기 위한 미션을 기획하는 AI입니다.
                아래 조건에 맞는 미션을 총 %d개 생성하세요.
                
                1. 미션 카테고리: "%s" — 이 카테고리에 맞는 환경 관련 미션을 작성하세요.
                2. 미션 타입: "%s" — DAILY 또는 WEEKLY 중 하나이며, 미션의 기간과 반복 주기를 나타냅니다.
                3. 보상 포인트: DAILY는 20~100점, WEEKLY는 100~300점 범위에서 난이도에 따라 차등 부여하세요.
                4. 난이도는 카테고리와 타입을 종합적으로 고려하여 LOW, MIDDLE, HIGH 중 하나로 설정하세요.
                5. 난이도를 적절히 분배해서 난이도가 골고루 분포 할 수 있도록 하세요.
                
                조건을 명확히 반영하고, 실제 시민이 수행 가능하면서도 환경 보호 효과가 있는 미션으로 작성해주세요.
                
                """.formatted(
                size,
                missionCategory.getDescription(),
                missionType.name()
        );
    }

    /**
     * 조건은 인자로 받고 미션 생성
     *
     * @param missionCategory 생성할 미션 카테고리
     * @param missionType     생성할 미션 타입
     * @param size            생성할 미션의 갯수
     */
    public List<AIMissionDto> generateMissionsWithAI(MissionCategory missionCategory, MissionType missionType,
                                                     int size) {
        String prompt = setMissionPrompt(missionCategory, missionType, size);

        try {
            StructuredChatCompletionCreateParams<AIMissionListResponseDto> createParams = ChatCompletionCreateParams.builder()
                    .model(ChatModel.GPT_4_1)
                    .maxCompletionTokens(2048)
                    .responseFormat(AIMissionListResponseDto.class)
                    .addAssistantMessage(prompt)
                    .build();
            return openAIClient.chat().completions().create(createParams).choices().stream()
                    .flatMap(choice -> choice.message().content().stream())
                    .flatMap(aiMissionListResponseDto -> aiMissionListResponseDto.getMissions().stream())
                    .toList();
        } catch (Exception e) {
            throw new AICreationExceptions("AI 기반 미션 생성중 오류");
        }


    }

    @Transactional
    public List<Mission> saveMission(List<Mission> missionList) {
        return missionJPARepository.saveAll(missionList);
    }

    /**
     * 유사도 측정
     *
     * @param newMission          새로 생성된 미션
     * @param ExistingMissionList 기돈 미션 리스트
     * @return 문장을 비교하요 유사도가 5가 넘은 경우 false 반환
     */
    public boolean isNotSimilarityMission(AIMissionDto newMission, List<Mission> ExistingMissionList) {
        String title1 = newMission.getTitle();
        for (Mission existingMission : ExistingMissionList) {
            String title2 = existingMission.getTitle();
            double similarity = sentenceSimilarityService.checkSimilarity(title1, title2);
            if (similarity > 5) { // 유사도가 높음
                return false;
            }
        }
        return true;
    }

    /**
     * AI로 생성한 미션을 유사도 검사 후 DB에 저장
     *
     * @param aiMissionDtoList AI로 생성한 미션 리스트
     */

    @Transactional
    public List<MissionDto> createMission(List<AIMissionDto> aiMissionDtoList) {
        List<Mission> ExistingMissionList = missionJPARepository.findAll();

        List<Mission> aiMissionList = aiMissionDtoList.stream()
                .filter(v -> isNotSimilarityMission(v, ExistingMissionList)) // 유사도 필터링
                .map(v -> Mission.builder()
                        .title(v.getTitle())
                        .description(v.getDescription())
                        .type(v.getType())
                        .level(v.getLevel())
                        .category(v.getCategory())
                        .rewardPoints(v.getRewardPoints())
                        .build())
                .toList();

        List<Mission> saveMissionList = saveMission(aiMissionList);

        return saveMissionList.stream()
                .map(MissionDto::from)
                .toList();
    }
}
