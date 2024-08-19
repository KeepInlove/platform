import com.gxy.App;
import com.gxy.service.OpenAiService;
import jakarta.annotation.Resource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

/**
 * @Classname Test
 * @Date 2024/8/15
 * @Created by guoxinyu
 */
@SpringBootTest(classes = App.class)
public class AppTest {

    @Resource
    private OpenAiService openAiService;
    @Test
    public  void test() {
        String directoryPath = "D:\\temp\\file";
        File folder = new File(directoryPath);
//        String filePath="D:\\temp\\file\\【测试_苏州】智冬梅 10年以上.pdf";

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".pdf")) {
                // 读取文件获取对应的文件内容
                String pdfContent = readPDFFile(file.getPath());
                String prompt = "从三个反引号括起来的内容为简历信息内容，抽取对应的姓名(rname)，性别(rsex)，手机号(rphone)，年龄(rage)，工龄(rworkingage)，学历(rdegreetype)，到岗日期(rarrivaldate)，期望薪资(rexpectedsalary)，工作地点(rworkplace)，岗位(roperatingpost)，行业(rindustry)，工作起止日期(rwokdate)，工作职位(rjobposition)，公司名称(rorganization)，简历来源(rsource)，邀约岗位(rjobvacancy)，沟通时间(rctime)，沟通记录(rcommunication)，状态标签(rstatuslabels)，基地名称(rbase)，备注信息(rsourcetxt)这21个字段，如果抽取不到字段内容，则对应字段返回空字符串，返回值使用JSON格式。```" + pdfContent + "```";
                String result = openAiService.openAiReq(prompt);  // JSON 的字符串
                //result 转为 对应的实体类存储到数据库中
            }
        }
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
        String info="冯涵钰 电话： 17513733492  邮箱： 17513733492@163.com 政治面貌： 中共党员 意向岗位：测试工程师  薪资：面议 教育经历 2017.9-2021.7  郑州科技学院 计算机科学与技术  |  本科 工作经历 2021.6-2024.7  深圳市智腾达软件技术有限公司 测试工程师  |  技术部 相关技能 1.  熟悉软件测试的各种理论、软件的生命周期和软件测试流程； 2.  熟练使用 测试用例的设计方法 ，如：等价类、边界值、场景法、判断表法、因果图法、正交试验法等； 3.  熟练使用 bug  管理工具 - 禅道  对  bug  进行管理和追踪； 4.  熟练使用 版本管理工具  SVN ，了解  git; 5.  熟练掌握  linux  常用命令（查看日志、端口号、进程等）； 6.  熟悉  MySQL  数据库，能够熟练使用 SQL  语句 进行 增、删、改、查数据 的操作，熟悉多表查询，子查询等； 7.  熟练使用 抓包工具  fiddler 、 F12  进行抓包  ，了解  Charles ； 8.  熟练使用 Postman 、 Jmeter  等工具完成 接口 测试； 9.  熟练使用 jmeter  编写 性能测试 脚本； 10.  能够独立分析和定位系统性能瓶颈； 11.  能够出具性能测试报告，并给出相关优化建议； 12.  熟悉  Python  编程语言，会使用  Python  标准库和常用的第三方库，能写一些简单的逻辑代码； 13.  熟练使用 Python  +Selenium  +  Pytest  框架进行 ui  自动化测试 ； 14.  熟练使用 python+pytest+requests  接口自动化测试框架实现 接口自动化测试 。 15.  能够使用 allure  框架生成  html  格式的 自动化测试报告 ，并进行分析； 16.  熟悉常用的  adb  操作命令 。 b9ea622c5751042e1nV_09q4GFJYw4W7VPyWWOaqmfPQNBJn b9ea622c5751042e1nV_09q4GFJYw4W7VPyWWOaqmfPQNBJnnull项目经历 2023. 09 -2024. 0 7  乾尊商城（ web+app ） 项目简介： 乾尊商城是一款移动购物软件，作为一个全面而先进的网络购物平台，承载了下单、订单查询、商品搜索、产品评价、 促销活动及团购等功能。同时汇聚数万个精选实惠商品，轻松挑选你所需的低价大牌商品。专业买手为您甄选，满足您的生 活购物需求。 项目模块： 商城首页、搜索框、抢购、热销、天天领券、积分、购物车、我的优惠券、订单、支付模块、会员列表管理模块等。 负责模块： 主要负责首页页面、搜索框、抢购、轮播图、支付模块、购物车模块、优惠券模块等。 岗位职责： •  需求分析： 根据业务需求文档进行详细的需求分析，参与需求评审会议，确保开发团队对业务需求有充分的理解，并提 出合理化建议。 •  业务逻辑梳理 ： 充分理解业务逻辑 ， 熟悉业务流程 ， 使用专业工具 （ 如  XMind ） 梳理测试点 ， 确保测试的全面性和准确 性。 •  测试用例设计： 根据业务逻辑及测试点设计编写测试用例，确保测试用例的完整性和有效性。 •  Bug  管理： 使用项目管理工具（如禅道）对发现的  bug  进行提交、跟踪和管理，确保  bug  得到及时修复。 •  数据校验： 使用  SQL  语句对数据库落库数据进行校验，确保数据的准确性和一致性。 •  前后端  bug  分析： 使用抓包工具（如  Fiddler ）协助分析、定位前后端  bug ，提高  bug  修复效率。 •  日志查看与缺陷定位： 使用  Linux  命令进行日志的查看以协助定位相关缺陷，快速解决问题。 •  接口测试与压力测试 ： 使用  Jmeter  等工具进行接口测试和压力测试 ， 编写测试脚本 ， 设置测试场景 ， 并分析性能指标 ， 确保平台的稳定性和性能。 •  自动化测试： 使用  Python+Selenium+Pytest  框架，编写  UI  自动化脚本进行回归测试，提高测试效率和质量。 •  测试报告编写： 编写详细的测试报告，总结测试过程中的问题和改进建议，为项目迭代和优化提供有力支持。 202 3 . 0 1-2023. 08  鑫用钱（ web+app ） 项目简介： 鑫用钱是科技导向智能化信贷平台，利用大数据， AI  技术，解决用户借钱难问题，实现信用变现，打造快速安全的一站式 信贷服务平台。获取借款额度简单，仅需验证身份证、绑定银行卡等即可获取额度。额度灵活，期限灵活。 项目模块： 登录、注册、首页  、个人中心、  帮助中心、实名认证  、银行卡管理  、额度申请、  借款申请、我要还款等模块。 负责模块： 主要负责 个人中心、实名认证、额度申请、借款申请、我要还款等。 岗位职责： •  需求分析： 查看需求文档，并参与需求评审会议。 •  测试用例设计： 熟悉业务流程，并梳理测试点；根据测试点编写测试用例，并参加用例评审会议。 •  Bug  管理： 执行测试用例，发现  bug  使用禅道提交  bug ，并追踪。 •  数据校验： 使用  SQL  语句对数据库落库数据进行校验。 •  前后端  bug  分析： 通过使用  Fiddler  进行抓包，定位前后端  bug 。 •  接口测试： 使用  Jmeter  编写接口测试脚本，根据  API  文档编写测试用例进行接口测试。 b9ea622c5751042e1nV_09q4GFJYw4W7VPyWWOaqmfPQNBJnnull•  性能测试： 使用  jmeter  进行性能测试，并分析相关性能指标。 •  测试报告编写： 编写提交相关的测试报告。 2022. 03 -202 2 . 12  智云商城（ web+app ） 项目简介： 云商城项目是为了实现生产制造企业购买原材料的互联互通，保证电机产业市场经济稳定发展，打击恶意竞价产品和假 冒伪劣原材料而发展出来的一个线上集采平台，一方面可以维护本公司线下客户购买电机生产制造原材料，另一方面通过商 家入驻的方式减少本公司采购原材料时不必要的金钱和时间消耗。用户可以通过平台认证，自由购买商品及发布需求公告， 商家亦可入驻平台，支持平台发展，且能够在平台发布商品，实现生产资料电商一体化。 项目模块： 买家端（商城首页，商品商家导航栏，商品详情，购物车，我的订单，个人中心等）； 卖家端（商品模版维护，商品维护 ( 上下架 ) ，员工管理，订单管理，消息后台等）； 后台端（主要是用来维护自营商品发布和店铺入驻审核相关操作的）。 负责模块： 主要负责买家端的导航栏、购物车、我的订单以及卖家端的员工管理、订单管理、消息后台 等。 岗位职责： •  需求理解： 参与需求评审，深入理解业务需求，制定测试策略。 •  测试设计： 设计全面覆盖业务场景的测试用例。 •  系统测试与问题跟踪 ：执行测试用例，对模块功能进行详细测试。使用禅道工具提交并跟踪管理  bug 。 •  数据验证： 利用  SQL  语句验证数据准确性。 •  接口测试： 使用  Fiddler  和  Postman  进行接口测试。 •  测试报告与经验总结： 编写测试报告，总结测试经验。对测试过程进行复盘，提炼有效测试方法。 2021. 0 6-2022. 02  智审行云 （ web ） 项目简介： 该项目旨在为公司开发一套集成的办公自动化管理系统，以提升工作效率和流程管理。系统主要功能包括文档管理、流 程审批、会议管理、任务分配、通讯工具等。该系统可以使工作更加效率 , 可以随时随地的查看工作进度 , 处理事务。 项目模块： 打卡、个人办公、工作流程、员工管理、产品管理、日报记录、行政办公和系统管理、假期审批等。 负责模块： 主要负责 打卡、工作流程、产品管理、日报管理等。 岗位职责： •  需求理解： 根据需求文档进行需求分析，参与需求评审会议并提出合理化建议。 •  测试设计： 使用  XMind  梳理测试点并根据测试点设计测试用例；参与测试用例评审、执行测试用例。 •  Bug  管理 ： 通过禅道提交  Bug, 跟踪  Bug, 直到  Bug  关闭。 •  数据验证： 使用相应  SQL  语句进行数据库落库数据的校验。 •  测试报告与经验总结： 复盘测试中发现的问题并总结编写相关测试报告。 自我评价 •  自律、 学习能力强 ，容易接受并理解新知识、新事物； b9ea622c5751042e1nV_09q4GFJYw4W7VPyWWOaqmfPQNBJnnull•  有责任心 ， 工作 细心 ，做事有耐心； •  逻辑性强，语言表达能力清晰，有 较强的沟通能力 ； •  遇到问题时，善于发现问题根源，能够很好的归纳和总结问题； •  思维严谨，善于思考，具备独立分析和解决问题的能力，能 独立的完成测试项目 ； •  工作态度严谨认真， 重视团队合作 ，有执行力。 ";
        String prompt = "从三个反引号括起来的内容为简历信息内容，抽取对应的姓名(rname)，性别(rsex)，手机号(rphone)，年龄(rage)，工龄(rworkingage)，学历(rdegreetype)，到岗日期(rarrivaldate)，期望薪资(rexpectedsalary)，工作地点(rworkplace)，岗位(roperatingpost)，行业(rindustry)，工作起止日期(rwokdate)，工作职位(rjobposition)，公司名称(rorganization)，简历来源(rsource)，邀约岗位(rjobvacancy)，沟通时间(rctime)，沟通记录(rcommunication)，状态标签(rstatuslabels)，基地名称(rbase)，备注信息(rsourcetxt)这21个字段，如果抽取不到字段内容，则对应字段返回空字符串，返回值使用JSON格式。```" + info + "```";
        openAiService.openAiReq(prompt);
    }
}
