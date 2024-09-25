import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.gxy.App;
import com.gxy.service.OpenAiService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.ChatOptionsBuilder;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @Classname Test
 * @Date 2024/8/15
 * @Created by guoxinyu
 */
@Slf4j
@SpringBootTest(classes = App.class)
public class AppTest {

    @Resource
    private OpenAiService openAiService;

    @Autowired
    private OpenAiChatModel openAiChatModel;

//    @Autowired
//    private ChatClient chatClient;

    @Test
    public void test() {
//        String directoryPath = "D:\\uploads\\fef7c561-2a49-4b46-be12-fd4335971bc9.pdf";
//        File folder = new File(directoryPath);
//        for (File file : folder.listFiles()) {
//            if (file.isFile() && file.getName().endsWith(".pdf")) {
                // 读取文件获取对应的文件内容
                String pdfContent = readPDFFile("D:\\work\\data\\WXWork\\1688857822605829\\Cache\\File\\2024-08\\1.pdf");
                log.info("PDF文件:{}",pdfContent);
//                String prompt = "从三个反引号括起来的内容为简历信息内容，抽取对应的姓名(rname)，性别(rsex)，手机号(rphone)，年龄(rage)，工龄(rworkingage)，学历(rdegreetype)，到岗日期(rarrivaldate)，期望薪资(rexpectedsalary)，工作地点(rworkplace)，岗位(roperatingpost)，行业(rindustry)，工作起止日期(rwokdate)，工作职位(rjobposition)，公司名称(rorganization)，简历来源(rsource)，邀约岗位(rjobvacancy)，沟通时间(rctime)，沟通记录(rcommunication)，状态标签(rstatuslabels)，基地名称(rbase)，备注信息(rsourcetxt)这21个字段，如果抽取不到字段内容，则对应字段返回空字符串，返回值使用JSON格式。```" + pdfContent + "```";
//                String result = openAiService.openAiReq(prompt);  // JSON 的字符串
                //result 转为 对应的实体类存储到数据库中
//            }
//        }
    }

    /**
     * @return PDF文件内容
     */
    public static String readPDFFile(String pdfFilePath) {
        String textContent = "";
        //本地PDF文件路径
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            textContent = stripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textContent;
    }


    @Test
    public void test2() {
        String prompt="现在是几点?当前天气怎么样?";
        String string = openAiService.openQwAiReq(null,prompt,null);
        System.out.println(string);
    }

    public static GenerationParam createGenerationParam(List<Message> messages) {
        return GenerationParam.builder()
                .model("qwen2-72b-instruct")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .apiKey("sk-d910833e4e2f43f6967ca49117823506")
                .topP(0.8)
                .build();
    }

    public static GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.call(param);
    }

//    public static void main(String[] args) {
//        try {
//            List<Message> messages = new ArrayList<>();
//
//            messages.add(createMessage(Role.SYSTEM, "You are a helpful assistant."));
//            for (int i = 0; i < 3;i++) {
//                Scanner scanner = new Scanner(System.in);
//                System.out.print("请输入：");
//                String userInput = scanner.nextLine();
//                if ("exit".equalsIgnoreCase(userInput)) {
//                    break;
//                }
//                messages.add(createMessage(Role.USER, userInput));
//                GenerationParam param = createGenerationParam(messages);
//                System.out.println("请求输入param："+param);
//                GenerationResult result = callGenerationWithMessages(param);
//                System.out.println("result："+result);
//                System.out.println("模型输出："+result.getOutput().getChoices().get(0).getMessage());
//                messages.add(result.getOutput().getChoices().get(0).getMessage());
//            }
//            System.out.println("messages:"+messages);
//        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
//            e.printStackTrace();
//        }
//        System.exit(0);
//    }
//
//    private static Message createMessage(Role role, String content) {
//        return Message.builder().role(role.getValue()).content(content).build();
//    }


    @Test
    public void testOpenAI() {
//        String results = openAiChatModel.call("你是谁?会什么");
//        log.info("results:{}", results);

//        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();

//        SystemMessage systemMessage = new SystemMessage("你是数据提取大师,你有很强的分辨能力");
//        messages.add(systemMessage);
//        messages.add(userMessage);
//        ChatResponse chatResponse = chatClient.prompt(new Prompt(messages)).call().chatResponse();

//        log.info("chatResponse:{}", chatResponse);
        UserMessage userMessage = new UserMessage("帮我使用Java写一个排序算法");
        String content = openAiChatModel.call(userMessage);
        System.out.println(content);


    }
}
