// ==UserScript==
// @name         0905-脚本2-JS
// @namespace    http://tampermonkey.net/
// @version      0.1
// @author       Duanxinyue
// @match        https://www.zhipin.com/*
// @match        https://passport.zhaopin.com/*
// @match        https://bbs.tampermonkey.net.cn/*
// @require      https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js


// @grant        unsafeWindow
// @grant        GM_getResourceText
// @grant        GM_addStyle
// @grant        GM_xmlhttpRequest

// @run-at       document-start
// ==/UserScript==


async function runIdleTasks() {
    // console.log('Running at document-idle');
    //文档完全加载时应运行的代码
    try {
         // 批量绑定 common1 的所有方法
        Object.keys(window.common1).forEach(function(key) {
            if (window.common1 &&  window.common1[key] && typeof window.common1[key] === 'function') {
                window.common1[key] = window.common1[key].bind(window.common1);
            }
        });

        common1.monitorXHR('wapi/zpjob/job/recJobList') // 推荐牛人---职位下拉框
        common1.monitorXHR('wapi/zprelation/friend/filterByLabel') // 沟通---全部-所有沟通者
        common1.monitorXHR('wapi/zppassport/user/accountStatus') // 获取当前boss面试官的userId
        
        if (window.top === window.self) {
            // 确保页面完全加载后再初始化
            initApplication();
        }

    } catch (error) {
        console.error("Failed to load scripts or styles:", error);
    }
}

function initApplication() {
    const appElement = document.createElement('div');
    appElement.id = 'hjzd_html';
    appElement.innerHTML = `
        <div id="floating-ball" class="draggable-ball">智</div>
        <div id="sliding-panel" class="sliding-panel">
            <div class="loginPersonDIV" >
                <div><span>面试官: </span>{{bossInfo.bossUserName}}</div>
                <div class="loginPersonDIV_right">
                    <el-button type="success" plain size="small" @click="checkGoutonBtn" style="margin-left:0" title="查看分析记录">查看</el-button>
                    <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" title="点击头像进行boss身份更新" @click="bossIdentityUpdateBtn"/>
                </div>
            </div>
            <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
                
                <el-tab-pane label="沟通" name="second">
                    <div class="secondDIV">
                        <div class="checkboxBox">
                        </div>
                        <div class="btnDIV1">
                            <el-button type="primary" plain size="small" @click="communicateBtnCurrent" style="margin-left:0" :disabled="(gtAutoAllDisabled && !goutongAutoStop) || (gtPersonalDisabled && !autoOrPersonalFinish)" :title="gtAutoAllDisabled?'自动分析进行中，不可点击，可点击下方停止按钮':((gtPersonalDisabled && !autoOrPersonalFinish)?'正在进行个人分析，请稍等...':'')"> ⌜{{bossInfo.bossUserName}}⌟ --自动分析</el-button>
                            <el-button type="primary" plain size="small" @click="communicateStopBtn" style="margin-left:0"  title="自动分析时才能点击停止" v-if="gtAutoAllDisabled && !goutongAutoStop"> ⌜{{bossInfo.bossUserName}}⌟ --自动分析-停止</el-button>
                            <el-button type="primary" plain size="small" @click="personalDetailsGTBtn2" :disabled="(gtPersonalDisabled && !autoOrPersonalFinish) || gtAutoAllDisabled" style="margin-left:0" :title="gtAutoAllDisabled?'自动分析进行中，不可点击，可点击停止按钮':((gtPersonalDisabled && !autoOrPersonalFinish)?'正在进行个人分析，请稍等...':'')">
                                <div>
                                 ⌜{{bossInfo.bossUserName}}⌟  对【 {{bossInfo.getPageGoutongClickPersoName || xxx }}】 进行 个人分析
                                <span v-if="(gtPersonalDisabled && !autoOrPersonalFinish)">（{{ countdown }}s）</span>
                                </div>
                            </el-button>
                        </div>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
    `;
    
    document.body.appendChild(appElement);

    // <el-button type="primary" plain size="small" @click="sendBtn">发送</el-button>
    // <el-tab-pane label="操作权限" name="first">
    // <el-button type="success" plain size="small" @click="checkGoutonBtn" style="margin-left:0" >查看</el-button>
    // </el-tab-pane>
    // <el-button type="primary" plain size="small" @click="communicateBtn" style="margin-left:0" :disabled="(gtAutoAllDisabled && !goutongAutoStop) || (gtPersonalDisabled && !autoOrPersonalFinish)" :title="gtAutoAllDisabled?'自动分析进行中，不可点击，可点击下方停止按钮':((gtPersonalDisabled && !autoOrPersonalFinish)?'正在进行个人分析，请稍等...':'')"> ⌜{{bossInfo.bossUserName}}⌟ --自动分析</el-button>
    // <el-button type="primary" plain size="small" @click="communicateBtnCurrent" style="margin-left:0" :disabled="(gtAutoAllDisabled && !goutongAutoStop) || (gtPersonalDisabled && !autoOrPersonalFinish)" :title="gtAutoAllDisabled?'自动分析进行中，不可点击，可点击下方停止按钮':((gtPersonalDisabled && !autoOrPersonalFinish)?'正在进行个人分析，请稍等...':'')"> ⌜{{bossInfo.bossUserName}}⌟ --当前内容开始自动分析</el-button>

    const App = {
        setup() {
            const common = window.common1;  
            const checked1 = Vue.ref(false);
            const checked2 = Vue.ref(false);
            const checked3 = Vue.ref(false);

            
            // 
            const activeName = Vue.ref('second')
            // 登录变量
            const ruleFormRef =Vue.ref(null)
            const ruleForm = Vue.reactive({
                username: '',
                password: '',
            })
            const rules = Vue.reactive({
                username: [{ required: true,message:'请填写账号', trigger: 'blur' }],
                password: [{ required: true,message:'请填写密码', trigger: 'blur' }],
            })
            const loginPerson=Vue.reactive({
                token:'',
                username:''
            })  // 登录信息
            const bossInfo=Vue.reactive({
                bossUserId:'',
                bossUserName:'',
                getPageGoutongClickPersoName:'',  // 当前沟通页面--点击的具体的沟通对象名字
                getPageGoutongClickPersoID:'',
                getPageLoginName:'', // 当前boss登录页的右上角登录姓名
                goutongMenuClickOperate:'', // 当前对沟通页面的操作--自动分析or个人分析
            })  // boss面试官-个人信息

            // 推荐牛人下的变量--------------------
            const input1 = Vue.ref(10); // 默认获取每个下拉框的简历信息个数
            const numTjSelectRound = Vue.ref(-1); // 推荐牛人---下拉框走的第几次 最终需要等于下拉框的总长度-1
            const numCurrentSelectPeopleRound = Vue.ref(0); // 推荐牛人---每次下拉框-下走过的简历数量
            
            // 沟通页相关变量
            // 个人分析
            const gtPersonalDisabled = Vue.ref(false);  // 沟通--个人分析的按钮是否禁用
            const countdown = Vue.ref(null);  // 倒计时秒数
            let countdownInterval = null;  // 计时器
            // 自动分析
            const gtAutoAllDisabled = Vue.ref(false);  // 沟通--自动分析的按钮是否禁用

            const autoOrPersonalFinish=Vue.ref(true)  // 自动或者个人是否已经完成了


            const messageOption = Vue.ref(null) // toastr的事件，为了清空用的

            const goutongAutoStop=Vue.ref(true) // 沟通----自动分析是否停止

            // 登录---------------------------------------------------------
            const submitForm = (formEl) => {
                if (!formEl) return
                formEl.validate((valid) => {
                    if (valid) {
                        const params={
                            username:ruleForm.username,
                            password:ruleForm.password,
                        }
                        const dataObj={
                            token:'假装登陆成功啦~~~~~~',
                            username:ruleForm.username
                        }
                        
                        const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                        dataOne['token']=dataObj.token
                        dataOne['username']=dataObj.username
                        common.storeData('hjzdTool', dataOne); // 存储数据

                        localStorage.setItem('hjzdInterfaceData',JSON.stringify(dataObj))
                        Object.assign(loginPerson,dataObj)
                        // 显示消息提示
                        ElMessage.closeAll();  // 清除所有显示的消息
                        ElMessage({
                            message: 'hjzd登录成功~~~',
                            type: 'success',
                            duration: 3000, // 消息3秒后自动关闭
                            customClass: 'custom-el-message' // 自定义类名
                        });
                    } else {
                       console.log('error submit!')
                    }
                })
            }
            // 重置
            const resetForm = (formEl) => {
                if (!formEl) return
                formEl.resetFields()
            }
            // 退出
            const exitBtn=()=>{
                
                const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                dataOne['token']=null
                dataOne['username']=null
                common.storeData('hjzdTool', dataOne); // 存储数据

                localStorage.removeItem('hjzdInterfaceData')
                ElMessage.closeAll();  // 清除所有显示的消息
                Object.assign(loginPerson, {
                    token:'',
                    username:''
                });
            }



            function getBossUserIdFun(){
                const username=document.querySelector('div.nav-item.nav-logout .label-name .user-name')
                if(username){
                    bossInfo.getPageLoginName=username?.innerText.trim()
                    const data = common.getData('hjzdTool') || {};
                    data.getPageLoginName=bossInfo.getPageLoginName
                    common.storeData('hjzdTool', data); // 存储数据
                }
                const data = common.getData('hjzdTool') || {};
                // boss 个人信息
                if (data && data.bossUserName == data.getPageLoginName) {
                    bossInfo.bossUserName = data.bossUserName;
                    bossInfo.bossUserId = data.bossUserId
                }else{
                    bossInfo.bossUserId= null
                    bossInfo.bossUserName= null
                }
            }
            getBossUserIdFun()

            // 监听页面中某个元素的文本变化
            const startObserving = () => {


                // 监听整个文档的点击事件
                document.body.addEventListener('click', (event) => {
                    
                    // 沟通模块下---点击触发的---获取当前click选中的人名
                    setTimeout(async() => {
                        goutongClickTabsName()
                    }, 100); // 延迟 100ms 确保内容已更新

                    //
                    // 监听---当前boss登录的姓名
                    common.waitForElement('.nav-item.nav-logout').then(res=>{
                        const username=document.querySelector('div.nav-item.nav-logout .label-name .user-name')
                        if(username){
                            bossInfo.getPageLoginName=username?.innerText.trim()
                            const data = common.getData('hjzdTool') || {};
                            data.getPageLoginName=bossInfo.getPageLoginName
                            common.storeData('hjzdTool', data); // 存储数据
                        }

                    }).catch(error => {
                        console.error(error);
                    });
                })

                // 沟通模块下---搜索选择触发的--是专门在沟通模块下搜索某个人时,不会触发click点击监听，但是能够及时拿到数据
                document.body.addEventListener('change', function(event) {
                    goutongClickTabsName()
                    // 你可以在这里添加你需要执行的逻辑
                });

                // 监听缓存数据的变化
                // Vue 中监听事件
                document.addEventListener('dataUpdated', (event) => {
                    const { key, value } = event.detail;
                    if (key === 'hjzdTool') {
                        const data = common.getData('hjzdTool') || {};
                        // boss 个人信息
                        if (data && data.bossUserName == data.getPageLoginName) {
                            bossInfo.bossUserName = data.bossUserName;
                            bossInfo.bossUserId = data.bossUserId 
                            bossInfo.goutongMenuClickOperate = data.goutongMenuClickOperate
                        }else{
                            bossInfo.bossUserId= null
                            bossInfo.bossUserName= null
                            bossInfo.goutongMenuClickOperate = null
                            // common.storeData('hjzdTool', {}); // 存储数据
                            // 没有和boss进行信息绑定，就跳到页面去进行信息获取
                            // isGetBossInformationFun() // 会造成死循环--不可用
                        }

                        if(data && data.getPageLoginName != data.bossUserName){
                            // 说明当前boss和当前tool绑定的boss信息不一致，此时需要统一 重新进行获取---【目前仅仅是根据姓名判断】
                            bossInfo.bossUserId= null
                            bossInfo.bossUserName= null
                            bossInfo.goutongMenuClickOperate = null
                            // common.storeData('hjzdTool', {}); // 存储数据
                            // 没有和boss进行信息绑定，就跳到页面去进行信息获取
                            // isGetBossInformationFun() // 会造成死循环--不可用
                        }

                        // 是否登录
                        // if (data && data.username !== loginPerson.username) {
                        //     loginPerson.token = data.token;
                        //     loginPerson.username = data.username
                        // }else{
                        //     toastr.clear()
                        //     loginPerson.token= null
                        //     loginPerson.username= null
                        //     toastr.error(
                        //         "没有登录hjzd-tool，请重新登录.",
                        //     );
                        // }

                        // 沟通--点击人页面
                        if(data && data.getPageGoutongClickPersoName){
                            bossInfo.getPageGoutongClickPersoName=data.getPageGoutongClickPersoName
                            bossInfo.getPageGoutongClickPersoID=data.getPageGoutongClickPersoID
                        }else{
                            bossInfo.getPageGoutongClickPersoName=null
                            bossInfo.getPageGoutongClickPersoID=null
                        }
                    }
                });
                
            };
            // 获取选中人名+一些状态空值 +是专门在沟通模块下搜索某个人时,不会触发click点击监听，但是能够及时拿到数据
            function goutongClickTabsName(){
                const data = common.getData('hjzdTool') || {};
                let menu = $('.menu-chat dt a');
                if (menu && menu.length > 0) {
                    if (menu[0] && menu[0].classList.contains('router-link-active')) {
                        // 是在沟通菜单页面下 class="geek-item selected"
                        // 添加延迟，确保内容加载完成
                        // await new Promise(resolve => setTimeout(resolve, 500));
                        setTimeout(()=>{
                            const getPageGoutongClickPersoName=document.querySelector('.user-container div.geek-item.selected .geek-name')?.innerText.trim()
                            data.getPageGoutongClickPersoName=getPageGoutongClickPersoName // 但是在沟通页面下，进行内部的tabs切换，获取到的是上一个tabs页的内容文本，就需要上述的tabs检测方法
                            data.getPageGoutongClickPersoID = document.querySelector('.geek-item.selected')?.getAttribute('data-id')?.split('-')[0]; // id
                            common.storeData('hjzdTool', data); // 存储数据
                        }, 100)
                        
                    }else{
                        data.getPageGoutongClickPersoName = ''
                        data.getPageGoutongClickPersoID = ''
                        common.storeData('hjzdTool', data); // 存储数据
                        
                        // 取消倒计时并恢复按钮可用状态
                        clearInterval(countdownInterval);
                        gtPersonalDisabled.value = false; // 
                        gtAutoAllDisabled.value = false;
                        goutongAutoStop.value=true 
                        autoOrPersonalFinish.value=true
                        communicateStopBtn() // 如果再进行自动分析，可是当前页面可能被面试官误触到其他地方，则需要停止自动分析
                    }
                }else{
                    // 倒计时取消----------------
                    // 取消倒计时并恢复按钮可用状态
                    clearInterval(countdownInterval);
                    gtPersonalDisabled.value = false; // 
                    gtAutoAllDisabled.value = false;
                    autoOrPersonalFinish.value=true
                }
            }

            // 监听 bossUserName 和 getPageGoutongClickPersoName 变化
            Vue.watch(
            () => [bossInfo.bossUserName, bossInfo.getPageGoutongClickPersoName],
            (newValues, oldValues) => {
                // 取消倒计时并恢复按钮可用状态
                clearInterval(countdownInterval);
                gtPersonalDisabled.value = false;
                autoOrPersonalFinish.value=true
            }
            );


            // tabs 切换---------------------------------------------------------
            const handleClick = (tab, event) => {
            }

            // 跳到其他网页面
            function jumpOtherWebpage(){
                // 使用 window.open 打开新窗口并跳转到指定 URL
                window.open('http://10.253.50.31:8091/fullCaseAnalysis/#/login', '_blank');
            }

            // 职位管理
            function positionManBtn1(){}

            // 推荐牛人-点击---------------------------------------------------------
            function confirmationBtn() {
                isGetBossInformationFun().then(data => {
                    // 进行后续处理
                    // return alert('暂时不允许点击')
                    // 当前的操作点击-
                    const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                    dataOne['当前menu']='推荐牛人'
                    common.storeData('hjzdTool', dataOne); // 存储数据
                    getTuijianniurenFunOne.firstStep()
                }).catch(error => {
                    console.error('出错了:', error);
                });
            }
            // 推荐牛人-进入到个人详细页点击点击---------------------------------------------------------
            function personalDetailsTJNRBtn(){
                isGetBossInformationFun().then(data => {
                    // 进行后续处理
                    $(document).ready(() => {
                        let iframe = document.querySelector('iframe[name="recommendFrame"]')

                        if (iframe) {
                            // 
                            let iframeDocument = iframe.contentDocument || iframe.contentWindow.document;
                            getTuijianniurenFunOne.fourStep(iframeDocument)
                        }else{
                            alert('请先进入到具体的个人简历页面')
                        }
                    })
                }).catch(error => {
                    console.error('出错了:', error);
                });

            }
            // 沟通--点击---------------------------------------------------------
            // 自动轮播存储
            function communicateBtn(){
                gtAutoAllDisabled.value=true // 已经点了自动分析-此时这个按钮禁用，停止按钮展示并可以点击
                goutongAutoStop.value=false
                autoOrPersonalFinish.value=false

                const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                dataOne['当前menu']='沟通'
                dataOne['goutongMenuClickOperate']='autoSrcollAnalysis'
                dataOne['goutongNeedAutomaticAll']='不需要' // tool进行一键触发自动轮播获取收集所有信息--页面有刷新 需要做判断
                common.storeData('hjzdTool', dataOne); // 存储数据

                isGetBossInformationFun().then(data => {
                    getGoutongFunTwo.firstStep(false)
                }).catch(error => {
                    console.error('出错了:', error);
                });
                
            }
            // 自动轮播存储--当前内容--不对沟通页进行刷新
            function communicateBtnCurrent(){
                gtAutoAllDisabled.value=true // 已经点了自动分析-此时这个按钮禁用，停止按钮展示并可以点击
                goutongAutoStop.value=false
                autoOrPersonalFinish.value=false

                const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                dataOne['当前menu']='沟通'
                dataOne['goutongMenuClickOperate']='autoSrcollAnalysis'
                dataOne['goutongNeedAutomaticAll']='不需要' // tool进行一键触发自动轮播获取收集所有信息--页面有刷新 需要做判断
                common.storeData('hjzdTool', dataOne); // 存储数据

                isGetBossInformationFun().then(data => {
                    getGoutongFunTwo.firstStep(true) // 不对沟通页进行刷新
                }).catch(error => {
                    console.error('出错了:', error);
                });
            }
            // 自动分析--停止
            function communicateStopBtn(){
                gtAutoAllDisabled.value=false // 已经点了自动分析停止按钮-此时这个按钮需要消失，展示自动分析按钮并且可以点击（但是不一定能立马点击，因为可能正在自动分子停止中途）
                goutongAutoStop.value=true 
                autoOrPersonalFinish.value=true
                
                autoOrPersonalFinish.value=true //  没有获取完，但是点击停止了需要设置成true

                const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                if(dataOne['goutongMenuClickOperate'] == 'autoSrcollAnalysis'){
                    ElMessage.closeAll();  // 清除所有显示的消息
                    ElMessage({
                        message: '正在进行自动分析停止，可能需要一些时间，请耐心等待（请勿操作页面）！',
                        type: 'error',
                        duration: 10000, // 消息3秒后自动关闭
                        customClass: 'custom-el-message' // 自定义类名
                    });
                }
                dataOne['当前menu']='沟通'
                dataOne['goutongMenuClickOperate']='autoSrcollAnalysisStop'
                dataOne['goutongNeedAutomaticAll']='不需要' // tool进行一键触发自动轮播获取收集所有信息--页面有刷新 需要做判断
                common.storeData('hjzdTool', dataOne); // 存储数据
                
                
            }
            // 沟通---进入到个人详细页点击点击
            // 单个-沟通
            function personalDetailsGTBtn2(){
                gtPersonalDisabled.value = true;
                gtAutoAllDisabled.value=false // 已经点了自动分析停止按钮-此时这个按钮需要消失，展示自动分析按钮并且可以点击（但是不一定能立马点击，因为可能正在自动分子停止中途）
                goutongAutoStop.value=true 
                autoOrPersonalFinish.value=true
                
                const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                dataOne['当前menu']='沟通'
                dataOne['goutongMenuClickOperate']='onePersonalAnalysis'
                common.storeData('hjzdTool', dataOne); // 存储数据

                // 开始倒计时
                countdown.value = 60;  // 设置倒计时时间为60秒
                countdownInterval = setInterval(() => {
                    countdown.value -= 1;
                    if (countdown.value <= 0) {
                    clearInterval(countdownInterval);
                    gtPersonalDisabled.value = false;  // 倒计时结束，恢复按钮可用
                    goutongAutoStop.value=true 
                    autoOrPersonalFinish.value=true
                    }
                }, 1000);

                isGetBossInformationFun().then(data => {
                    // 进行后续处理
                    // 获取带有 selected 类名的 geek-item 元素
                    const selectedElement = document.querySelector('.geek-item.selected');
                    if (selectedElement) {
                        // 获取 data-id 属性值
                        const dataId = selectedElement.getAttribute('data-id');
                        const uniqueId = dataId.split('-')[0]; // 提取key的值的前半部分
                        const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                        if(dataOne['沟通menu-gerenfenxi']){
                            if(dataOne['沟通menu-gerenfenxi'].some(itemOneClick=>itemOneClick==uniqueId) || dataOne['沟通menu--auto-click_id'].some(it=>it===uniqueId)){
                                //  清除所有之前的 通知
                                if(messageOption.value) messageOption.value.close(); // 手动关闭消息
                                // ElMessage.closeAll();  // 清除所有显示的消息
                                const geekName = document.querySelector('.geek-item .geek-name').innerText.trim();
                                messageOption.value=ElMessage({
                                    message: `今日已经对【${bossInfo.getPageGoutongClickPersoName}】进行过分析啦！`,
                                    type: 'warning',
                                    grouping: true, //  内容相同的 message 将被合并。
                                    duration: 3000, // 消息3秒后自动关闭
                                    // position: 'top-right', // 设置消息显示的位置为右上角
                                    // offset: 40, // 距离页面顶部 40px
                                    customClass: 'custom-el-message' // 自定义类名
                                });
                                return  
                            }else{
                                
                                //  清除所有之前的 通知
                                // 显示消息提示
                                if(messageOption.value) messageOption.value.close(); // 手动关闭消息
                                // ElMessage.closeAll();  // 清除所有显示的消息
                                messageOption.value=ElMessage({
                                    message: Vue.h('div', { style: 'line-height: 1; font-size: 14px;display:flex;align-item:center' }, [
                                        Vue.h('div', null, '正在对【'),
                                        Vue.h('span', { style: 'color: skyblue' }, `${bossInfo.getPageGoutongClickPersoName}`),
                                        Vue.h('div', null, '】的信息进行分析，面试官--'),
                                        Vue.h('span', { style: 'color: orange' }, `${bossInfo.bossUserName}`),
                                        Vue.h('div', null, ' 请稍等，请勿操作页面...'),
                                    ]),
                                    // message: `正在进行信息收集，(面试官--${bossInfo.bossUserName}) 请稍等...`,
                                    type: 'success',
                                    grouping: true, //  内容相同的 message 将被合并。
                                    duration: 600000, // 设置为 0 表示不自动关闭
                                    // position: 'top-right', // 设置消息显示的位置为右上角
                                    // offset: 40, // 距离页面顶部 40px
                                    icon:'Loading',
                                    customClass: 'custom-el-messageBlue' // 自定义类名
                                });
                                // 进行后续处理
                                // 接口判断 是否需要在线简历
                                const params={
                                    platformCadidateId: bossInfo.getPageGoutongClickPersoID,
                                    platformId: 84 // 因为后端定义的是整数 'boss'
                                }
                                common.request('post', urlRootPath+'/candidate/selectCandidateList', JSON.stringify(params))
                                    .then(response => {
                                       if(response.result=="success"){
                                            const data=JSON.parse(response.data)
                                            // 500表示这个人不在数据库中，需要传，200表示人在库里，不需要传在线简历
                                            if(data.code==500){
                                                // console.log('需要在线简历---------之前没入库过')
                                                getGoutongFunTwo.threeStepDetailFun('需要获取在线简历信息')  //  是否需要获取在线简历信息和获取对话内容 or 只需要获取对话内容
                                            }else if(data.code==200){
                                                // console.log('不需要在线简历---------之前入库过')
                                                getGoutongFunTwo.threeStepDetailFun('不需要在线简历，只需要对话记录')  //  是否需要获取在线简历信息和获取对话内容 or 只需要获取对话内容
                                            }
                                       }else{
                                            ElMessage.closeAll();  // 清除所有显示的消息
                                            ElMessage({
                                                message: '判断是否入库接口报错！',
                                                type: 'error',
                                                duration: 10000, // 消息3秒后自动关闭
                                                customClass: 'custom-el-message' // 自定义类名
                                            });
                                       }
                                    })
                                    .catch(error => {
                                        console.error(error);
                                    });
                            }
                        }else{
                            
                            if(messageOption.value) messageOption.value.close(); // 手动关闭消息
                            // ElMessage.closeAll();  // 清除所有显示的消息
                            messageOption.value=ElMessage({
                                message: Vue.h('div', { style: 'line-height: 1; font-size: 14px' }, [
                                    Vue.h('div', null, '正在对【'),
                                    Vue.h('span', { style: 'color: skyblue' }, `${bossInfo.getPageGoutongClickPersoName}`),
                                    Vue.h('div', null, '】的信息进行分析，面试官--'),
                                    Vue.h('span', { style: 'color: orange' }, `${bossInfo.bossUserName}`),
                                    Vue.h('div', null, ' 请稍等，请勿操作页面...'),
                                ]),
                                type: 'success',
                                grouping: true, //  内容相同的 message 将被合并。
                                duration: 600000, // 设置为 0 表示不自动关闭
                                icon:'Loading',
                                customClass: 'custom-el-messageBlue' // 自定义类名
                            });
                            // 进行后续处理
                            // 接口判断 是否需要在线简历
                            const params={
                                platformCadidateId: bossInfo.getPageGoutongClickPersoID,
                                platformId:84 // 因为后端定义的是整数 'boss'
                            }
                            common.request('post', urlRootPath+'/candidate/selectCandidateList', JSON.stringify(params))
                                .then(response => {
                                    if(response.result=="success"){
                                        const data=JSON.parse(response.data)
                                        // 500表示这个人不在数据库中，需要传，200表示人在库里，不需要传在线简历
                                        if(data.code==500){
                                            getGoutongFunTwo.threeStepDetailFun('需要获取在线简历信息')  //  是否需要获取在线简历信息和获取对话内容 or 只需要获取对话内容
                                        }else if(data.code==200){
                                            getGoutongFunTwo.threeStepDetailFun('不需要在线简历，只需要对话记录')  //  是否需要获取在线简历信息和获取对话内容 or 只需要获取对话内容
                                        }
                                    }else{
                                        ElMessage.closeAll();  // 清除所有显示的消息
                                        ElMessage({
                                            message: '判断是否入库接口报错！',
                                            type: 'error',
                                            duration: 10000, // 消息3秒后自动关闭
                                            customClass: 'custom-el-message' // 自定义类名
                                        });
                                    }
                                })
                                .catch(error => {
                                    console.error(error);
                                });
                        }
                    } else {
                        // 跳到沟通页
                        let menu = $('.menu-chat dt a');
                        if (menu && menu.length > 0) {
                            if (menu[0] && menu[0].classList.contains('router-link-active')) {
                                ElMessage.closeAll();  // 清除所有显示的消息
                                ElMessage({
                                    message: '没有进入到具体的对话内容页！',
                                    type: 'error',
                                    duration: 3000, // 消息3秒后自动关闭
                                    // position: 'top-right', // 设置消息显示的位置为右上角
                                    // offset: 40, // 距离页面顶部 40px
                                    customClass: 'custom-el-message' // 自定义类名
                                });
                            } else {
                                common.simulateMouseMoveAndClick( menu[0],()=>{})
                                ElMessage.closeAll();  // 清除所有显示的消息
                                ElMessage({
                                    message: '进入到沟通页,请选择具体的面试者...',
                                    type: 'success',
                                    duration: 3000, // 消息3秒后自动关闭
                                    // position: 'top-right', // 设置消息显示的位置为右上角
                                    // offset: 40, // 距离页面顶部 40px
                                    customClass: 'custom-el-message' // 自定义类名
                                });
                            }
                        } else {
                            console.log('没有找到足够的元素进行点击');
                        }
                    }
                    // 进行后续处理
                }).catch(error => {
                    console.error('出错了:', error);
                });
            }
            // 同步当前面试官信息---------------------------------------------------------
            function bossIdentityUpdateBtn() {
                isGetBossInformationFun('同步当前面试官信息')
            }
            // 查看
            function checkGoutonBtn(){
                // 在新窗口中打开外部链接  http://localhost/candidate
                const url = `http://10.253.3.189:80/candidate?platformUserId=${bossInfo.bossUserId}`; // 替换为你要打开的外部链接
                window.open(url, '_blank'); // '_blank' 参数表示在新窗口中打开链接
            }
            //  发送文本
            function sendBtn(){
                // 找到目标 div (根据实际情况修改选择器)
                var editableDiv = document.querySelector('#boss-chat-editor-input');

                if (editableDiv) {
                    // 填充内容到 contenteditable div
                    editableDiv.innerHTML = '请问你可以发一下你的个人简历吗？ 我这边想要看一下，谢谢。';
                    
                    const inputBtn = document.querySelector('.submit.active');
                    if(!inputBtn) return console.log('没找到发送按钮')
                    inputBtn.click()
                    console.log('tool-模拟发送的文本')
                    // 如果需要触发输入事件（例如某些框需要输入事件来更新状态）
                    // var event = new Event('input', {
                    //     bubbles: true,
                    //     cancelable: true,
                    // });
                    // editableDiv.dispatchEvent(event);
                }
            }
            
            // 获取 推荐牛人---个人信息简历信息---方法------------------------------------------------------------------------------------------
            const getTuijianniurenFunOne = {
                // 第一步
                firstStep() {
                    numTjSelectRound.value++ // 当前的下拉框index
                    numCurrentSelectPeopleRound.value=1

                    let menu = $('.menu-recommend dt a');
                    if (menu && menu.length > 0) {
                        if (menu[0] && menu[0].classList.contains('router-link-active')) {
                            common.reloadPage()  // 刷新页面
                            // 剩下的就在页面刷新监听里操作了------在onMounted中进行了
                            // Vue.nextTick(() => {
                            //     // 延迟点击第一个
                            //     setTimeout(() => {
                            //         this.secondStep();
                            //     }, delay);
                            // })
                        } else {
                            // menu[0].click();
                            common.simulateMouseMoveAndClick( menu[0],()=>{
                                Vue.nextTick(() => {
                                    // 延迟点击第一个
                                    setTimeout(() => {
                                        this.secondStep();
                                    }, common.getRandomDelay(3000, 12000));
                                })
                            })
                        }
                    } else {
                        // console.log('没有找到《推荐牛人》菜单进行点击');
                    }
                },

                // 第二步
                secondStep() {
                    // 
                    $(document).ready(() => {
                        // 
                        let iframe = document.querySelector('iframe[name="recommendFrame"]');

                        if (iframe) {
                            // 
                            let iframeDocument = iframe.contentDocument || iframe.contentWindow.document;
                            let ulElement = iframeDocument.querySelector('.list-body .card-list');
                            if (ulElement) {
                                let liElements = ulElement.querySelectorAll('.card-item .candidate-card-wrap');
                                liElements.forEach((li, index) => {

                                    if (index == 0) {
                                        let cardInner = li.querySelector('.card-inner');
                                        // cardInner.click();
                                        common.simulateMouseMoveAndClick( cardInner,()=>{
                                            Vue.nextTick(() => {
                                                common.observeElement('.recommend-list-wrap .dialog-body', (resumeDialogElement) => {
                                                    if(resumeDialogElement){
                                                        this.thirdStep(iframeDocument);
                                                    }else{
                                                        console.log("------------------------------(即  今日查看人数已达上限！【免费权益20个，VIP权益80个】)------------------------------")
                                                    }
                                                }, iframeDocument);
                                            })
                                        })
                                        // Proceed to the third step
                                        // common.request("get", "http://10.253.3.236:8080/test/hello", null)
                                        //     .then((resultData) => {
                                        //         console.log("响应数据", resultData);
                                        //     });
                                    }
                                });
                            } else {
                                console.error('未找到 <ul> 元素（即 没有可推荐的信息列表）');
                                // toastr.error(
                                //     "I do not think that word means what you think it means.",
                                //     "Inconceivable!"
                                // );
                            }
                        } else {
                            console.error('未找到 iframe 元素');
                        }
                    });
                },

                // 第三步
                thirdStep(iframeDocument) {
                    // one---当前简历div
                    this.fourStep(iframeDocument).then(()=>{
                        // setTimeout(()=>{
                            // two----下一个简历
                            let nextDIV = iframeDocument.querySelector('.dialog-body .resume-next');
                            if(nextDIV){
                                if(numCurrentSelectPeopleRound.value < input1.value){
                                    // 继续获取
                                    // nextDIV.click()
                                    common.simulateMouseMoveAndClick( nextDIV,()=>{
                                        Vue.nextTick(() => {
                                            numCurrentSelectPeopleRound.value++ // 下一个简历-第几个了
                                            this.thirdStep(iframeDocument)  // 开始循环
                                            // this.fourStep(iframeDocument)
                                        })
                                    })
                                }else{
                                // 默认每个岗位获取 input1.value个简历--超出则换下一个下拉框简历
                                    this.nextSelectFun()  // 进行下一轮下拉框数据-简历信息列表
                                }

                            }else{
                                // 没有简历了----进行下一个下拉框列表数据
                                // numTjSelectRound.value 
                                this.nextSelectFun() // 进行下一轮下拉框数据-简历信息列表
                            }
                        // },3000)
                    })
                    
                    
                            

                },
            
                // 推荐牛人--具体的内容获取
                fourStep(fatherDIV){
                    return new Promise((resolve,reject)=>{
                        let contentDIV = fatherDIV.querySelector('.resume-item-content');

                        if(!contentDIV){
                            return alert('需要您打开一个具体的简历页')
                        } 
                        // const dataObj=JSON.parse(localStorage.getItem('hjzdInterfaceData') || null) || {}
                        const dataObj=common.getData('hjzdTool') || {}; // 获取数据

                        const jobSelectList=dataObj.jobSelectList

                        const onePersion={
                            // '头像': contentDIV.querySelector('.figure img').src, // 头像--图片路径
                            'name': contentDIV.querySelector('.geek-name').innerText,  // 姓名--顾先生
                            'gender': contentDIV.querySelector('i.icon-gender').classList.contains('iboss-icon_man') ? '男':'女', // 性别--男
                            // '状态': contentDIV.querySelector('.text-orange').innerText, // 状态--刚刚活跃
                            'age': contentDIV.querySelector('i.fz.fz-age')?.closest('.label-text').innerText, // 年龄---22岁
                            'workExperience': contentDIV.querySelector('i.fz.fz-experience')?.closest('.label-text').innerText, // 工作经验---24年应届生/10年以上
                            'education': contentDIV.querySelector('i.fz.fz-degree')?.closest('.label-text').innerText, // 学历---本科
                            'arrivalTime': contentDIV.querySelector('i.fz.fz-status')?.closest('.label-text').textContent.trim(), // 到岗时间---在校-月内到岗
                            'personalAdvantages': contentDIV.querySelector('div.text.selfDescription')?.innerHTML, // 个人优势描述---热爱标注刻苦钻研能吃苦耐劳
                            // '期望职位1': contentDIV.querySelectorAll('.info-labels .label-text')[0]?.innerText, // 苏州
                            // '期望职位2': contentDIV.querySelectorAll('.info-labels .label-text')[1]?.innerText, // 数据标注/AI训练师
                            // '期望职位3': contentDIV.querySelectorAll('.info-labels .label-text')[2]?.innerText, // 行业不限
                            // '期望职位4': contentDIV.querySelectorAll('.info-labels .label-text')[3]?.innerText, // 5-10k
                            professionalSkills: contentDIV.querySelector('.geek-professional-skill-wrap .section-content')?.innerText,// 专业技能---ATA职业技能证书驾驶证C1/熟练运用办公软件
                            // workExperiences:[], // 工作经历
                            jobId:jobSelectList[numTjSelectRound.value].encryptId, // 当前岗位下拉框的id
                            bossId:28640225, // 面试官的userId
                            resumeAttachment:'',
                            contactStatus:'',
                            contactLink:'',
                            // educationExperiences:[], // 教育经历
                        }
                        const resumeItems = contentDIV.querySelectorAll('.resume-item');
                        
                        const workExperiences = [];// 工作经历
                        const projectExperiences = [];// 项目经验
                        const educationExperiences = [];// 教育经历

                        // 遍历每个 resume-item，找到标题为 "工作经历" 的项 textContent innerText
                        resumeItems.forEach((resumeItem) => {
                            const titleElement = resumeItem.querySelector('.title');

                            if (titleElement && (titleElement.innerText.trim() === '期望职位' ||  titleElement.innerText.trim() === '最近关注')) {
                                onePersion.work_location=resumeItem.querySelectorAll('.info-labels .label-text')[0]?.innerText // 工作地点---苏州
                                onePersion.jobName=resumeItem.querySelectorAll('.info-labels .label-text')[1]?.innerText  // 职位名称----数据标注/AI训练师
                                onePersion.industry=resumeItem.querySelectorAll('.info-labels .label-text')[2]?.innerText // 行业---不限
                                onePersion.salary=resumeItem.querySelectorAll('.info-labels .label-text')[3]?.innerText //  薪资--- 5-10k
                            }
                            if (titleElement && titleElement.innerText.trim() === '工作经历') {
                                // 找到所有工作经历项
                                const historyItems = resumeItem.querySelectorAll('.history-item');
                                // 遍历每个工作经历项
                                historyItems.forEach((item) => {
                                    // 提取时间段
                                    const period = item.querySelector('.period')?.innerText.trim();

                                    // 提取公司名称和职位
                                    const companyName = item.querySelector('.name a .helper-text')?.innerText.trim() || item.querySelector('span:first-child').innerText.trim();
                                    const position = item.querySelector('.name span:last-child')?.innerText.trim();

                                    // 提取描述
                                    const description = item.querySelector('.item-text .text')?.innerHTML;

                                    // 创建工作经历对象
                                    const obj = {
                                        duration:period,  // 时间----
                                        company:companyName, // 公司---
                                        position:position, // 职位---
                                        workDescription: description  // 工作描述---
                                    };

                                    // 将工作经历对象添加到数组中
                                    workExperiences.push(obj);
                                });
                            }
                            
                            if (titleElement && titleElement.innerText.trim() === '项目经验') {
                                // 找到所有项目经验项
                                const historyItems = resumeItem.querySelectorAll('.history-item');
                                // 遍历每个项目经验项
                                historyItems.forEach((item) => {
                                    // 
                                    const childNodes = item.querySelector('.history-item .name')?.childNodes
                                    const name = childNodes[0].textContent.trim();
                                    const role = childNodes[2].textContent.trim();
                                    const duration = item.querySelector('.history-item .period')?.innerText.trim();
                                    const description = item.querySelector('.item-text .project-content')?.innerHTML;

                                    // 创建项目经验对象
                                    const obj = {
                                        projectName:name,  // 项目名称----
                                        projectRole:role, // 项目职责---
                                        projectDuration:duration, // 项目时间---
                                        projectDescription: description  // 项目描述---
                                    };

                                    // 将项目经验对象添加到数组中
                                    projectExperiences.push(obj);
                                });
                            }
                            
                            if (titleElement && titleElement.innerText.trim() === '教育经历') {
                                // 找到所有教育经历项
                                const historyItems = resumeItem.querySelectorAll('.history-item');
                                // 遍历每个教育经历项
                                if (historyItems.length > 0) {
                                    const itemEducation=historyItems[0]
                                    if(itemEducation){
                                        onePersion.school=itemEducation.querySelector('h4.name b')?.innerText  // 学校---
                                        onePersion.major=itemEducation.querySelector('.major')?.innerText  // 专业---
                                        onePersion.schoolExperience=itemEducation.querySelector('.school-item .content')?.innerText  // 在校经历---
                                        // onePersion['学历']= Array.from((itemEducation.querySelector('h4.name')).childNodes)
                                        // .filter(node => node.nodeType === Node.TEXT_NODE) // 过滤出文本节点
                                        // .map(node => node.textContent.trim()) // 去除空白字符
                                        // .filter(text => text.length > 0) // 去除空字符串
                                        // .find(text => ['大专', '本科', '硕士', '博士'].includes(text)) || '未知'; // 查找学历
                                    }
                                }
                            }
                        });
                        onePersion.workExperiences=workExperiences
                        onePersion.projectExperiences=projectExperiences
                        // onePersion.educationExperiences=educationExperiences
                        resolve(onePersion)
                        // 调接口存储数据
                        // common.request('post', 'http://10.253.3.236:8080/candidate/save', onePersion)
                        //     .then(response => {
                        //         resolve(onePersion)
                        //     })
                        //     .catch(error => {
                        //         console.error(error);
                        //         reject()
                        //     });
                    })
                },

                // 下一个下拉框选项的简历获取
                nextSelectFun(){
                    // const dataObj=JSON.parse(localStorage.getItem('hjzdInterfaceData') || null) || {}
                    const dataObj=common.getData('hjzdTool') || {}; // 获取数据

                    const jobSelectList=dataObj.jobSelectList

                    if( jobSelectList && numTjSelectRound.value+1 <= jobSelectList.length-1){
                        const jobIdNew=jobSelectList[numTjSelectRound.value+1].encryptId
                        common.monitorXHR('wapi/zpjob/rec/geek/list',jobIdNew) // 推荐牛人---列表信息
                        // 开始重复操作了
                        Vue.nextTick(() => {
                            this.firstStep()
                        })
                    }else{
                        // 否则--已经是下拉框的最后一条数据了--无简历列表了--不做操作
                    }
                }

            };
            // 获取  沟通---页面个人信息+对话记录---方法----------------------------------------------------------------------------------------
            const getGoutongFunTwo = {
                // 第一步
                // 第一步
                async firstStep(isCurrentNoAllContent) {
                    try {
                        let menu = $('.menu-chat dt a');

                        if (menu && menu.length > 0) {
                            // 当前已经在沟通页面中了----------
                            if (menu[0] && menu[0].classList.contains('router-link-active') && !isCurrentNoAllContent) {
                                const dataOne = common.getData('hjzdTool') || {}; // 获取数据
                                dataOne['当前menu'] = '沟通';
                                dataOne['goutongNeedAutomaticAll'] = '需要-因为页面有刷新'; // tool进行一键触发自动轮播获取收集所有信息--页面有刷新 需要做判断
                                common.storeData('hjzdTool', dataOne); // 存储数据

                                ElMessage.closeAll();  // 清除所有显示的消息
                                ElMessage({
                                    message: '需要刷新页面进行所有内容的获取来开始自动分析！（请勿操作）',
                                    type: 'warning',
                                    duration: 0, // 消息3秒后自动关闭
                                    customClass: 'custom-el-message' // 自定义类名
                                });

                                // 延迟刷新页面
                                await this.delayAndRefreshPage(common.getRandomDelay(4000, 7000));

                                // 等待页面加载完成，再继续执行第二步--- 已经在load中监听进行了
                            } else {
                                const dataOne = common.getData('hjzdTool') || {}; // 获取数据
                                dataOne['当前menu'] = '沟通';
                                dataOne['goutongNeedAutomaticAll'] = '不需要'; // tool进行一键触发自动轮播获取收集所有信息--页面有刷新 需要做判断
                                common.storeData('hjzdTool', dataOne); // 存储数据

                                ElMessage.closeAll();  // 清除所有显示的消息
                                ElMessage({
                                    message: '正在进行自动分析，需大量时间，需耐心等待（请勿操作页面）....',
                                    type: 'warning',
                                    duration: 0, // 消息3秒后自动关闭
                                    customClass: 'custom-el-message' // 自定义类名
                                });
                                if (goutongAutoStop.value) {
                                    ElMessage.closeAll();  // 清除所有显示的消息
                                    ElMessage({
                                        message: `自动分析已停止！`,
                                        type: 'warning',
                                        duration: 3000, // 消息3秒后自动关闭
                                        customClass: 'custom-el-message' // 自定义类名
                                    });
                                    return
                                }
                                if(!goutongAutoStop.value){ // 自动分析-停止
                                    await common.simulateMouseMoveAndClick(menu[0]);
                                    await this.delayAndExecute(common.getRandomDelay(4000, 7000), () => this.secondStep());
                                }
                            }
                        } else {
                            // console.log('没有找到足够的元素进行点击');
                        }
                    } catch (error) {
                        console.error('firstStep encountered an error:', error);
                    }
                },

                // 第二步
                async secondStep() {
                    try {
                        if (goutongAutoStop.value) {
                            ElMessage.closeAll();  // 清除所有显示的消息
                            ElMessage({
                                message: `自动分析已停止！`,
                                type: 'warning',
                                duration: 3000, // 消息3秒后自动关闭
                                customClass: 'custom-el-message' // 自定义类名
                            });
                            return
                        }
                        common.closeAdvertisementFun();
                        const MAX_VISIBLE_ITEMS = 40; // 每次展示的最大项目数
                        const scrollStep = 1300; // 每次滚动的像素距离

                        const scrollContainer = document.querySelector('.user-list.b-scroll-stable');

                        if (!scrollContainer) {
                            // console.log("滚动容器未找到");
                            return;
                        }

                        await this.autoScrollAndProcess(scrollContainer, scrollStep);
                    } catch (error) {
                        console.error('secondStep encountered an error:', error);
                    }
                },

                // 自动滚动并处理逻辑
                async autoScrollAndProcess(scrollContainer, scrollStep) {
                    let isLeftScrollBottom=false // 是否左侧沟通者滚动到底部了
                    while (true) {
                        // 检查 goutongAutoStop 是否为 true，如果是，则退出循环
                        if (goutongAutoStop.value) {
                            ElMessage.closeAll();  // 清除所有显示的消息
                            ElMessage({
                                message: `自动分析已停止！`,
                                type: 'warning',
                                duration: 3000, // 消息3秒后自动关闭
                                customClass: 'custom-el-message' // 自定义类名
                            });
                            break; // 退出循环
                        }

                        await Vue.nextTick();
                        const newItems = Array.from(document.querySelectorAll('[role="listitem"]'));
                        await this.clickItemsSequentially(newItems,isLeftScrollBottom);

                        const initialScrollTop = scrollContainer.scrollTop;
                        scrollContainer.scrollBy(0, scrollStep);
                        this.triggerScrollEvent(scrollContainer);

                        // 添加延迟，确保内容加载完成
                        await new Promise(resolve => setTimeout(resolve, 1000));
                        
                        await Vue.nextTick();
                        const currentScrollTop = scrollContainer.scrollTop;

                        if (currentScrollTop === initialScrollTop) {
                             // 已到达页面底部
                            isLeftScrollBottom=true
                            ElMessage.closeAll();  // 清除所有显示的消息
                            ElMessage({
                                message: `自动分析完毕！`,
                                type: 'success',
                                duration: 3000, // 消息3秒后自动关闭
                                // position: 'top-right', // 设置消息显示的位置为右上角
                                // offset: 40, // 距离页面顶部 40px
                                customClass: 'custom-el-message' // 自定义类名
                            });
                            // console.log('已到达页面底部或所有项目已处理完毕');

                            // 按钮禁用展示的判断
                            autoOrPersonalFinish.value=true //  滚动到底部了，并且人的信息都点击获取完了
                            goutongAutoStop.value=true //  滚动到底部了，并且人的信息都点击获取完了
                            
                            gtAutoAllDisabled.value=false  // 
                            gtPersonalDisabled.value=false
                            break;
                        }
                    }
                },
                
                // 确保页面真的滚动了-----------目的是：虽然我们可以模拟滚动并获取新的 DOM 元素，但如果页面本身没有触发相应的更新逻辑，那么可能会导致页面的实际显示内容没有变化
                triggerScrollEvent(element) {
                    const event = new Event('scroll', {
                        bubbles: true,
                        cancelable: true
                    });
                    element.dispatchEvent(event);
                },

                // 点击每个项目
                async clickItemsSequentially(items,isLeftScrollBottom) {
                    if(isLeftScrollBottom){
                        autoOrPersonalFinish.value=true //  滚动到底部了，并且人的信息都点击获取完了
                        goutongAutoStop.value=true //  滚动到底部了，并且人的信息都点击获取完了
                        
                        gtAutoAllDisabled.value=false  // 
                        gtPersonalDisabled.value=false
                        // 点击每个人之前先将前面的在线简历or 附件简历关闭掉，防止前面有没关掉的情况
                        const bossPopupClose = document.querySelector('.dialog-wrap .boss-popup__close');
                        if(bossPopupClose){
                            // 移除 ka 属性
                            bossPopupClose.removeAttribute('ka');
                            bossPopupClose.click()
                        } 
                        return 
                    }
                    for (let index = 0; index < items.length; index++) {
                        // 检查 goutongAutoStop 是否为 true，如果是，则退出循环
                        if (goutongAutoStop.value) {
                            // ElMessage.closeAll();  // 清除所有显示的消息
                            ElMessage({
                                message: `自动分析已停止，正在将当前信息分析完成，请耐心等待！`,
                                type: 'error',
                                duration: 0, // 消息3秒后自动关闭
                                customClass: 'custom-el-message' // 自定义类名
                            });
                            break; // 退出循环
                        }
                        // console.log('index--------',index)
                        // 点击每个人之前先将前面的在线简历or 附件简历关闭掉，防止前面有没关掉的情况
                        const bossPopupClose = document.querySelector('.dialog-wrap .boss-popup__close');
                        if(bossPopupClose){
                            // 移除 ka 属性
                            bossPopupClose.removeAttribute('ka');
                            bossPopupClose.click()
                        } 
                        await this.clickAndRequest(items[index], index, index === items.length - 1);
                    }
                },
                // 定义一个函数用于处理点击和接口调用
                async clickAndRequest(item, index, isArrayEndIndex) {
                    const geekItem = item.querySelector('.geek-item');
                    const geekName = geekItem.querySelector('.geek-name');
                    const keyId = geekItem.getAttribute('data-id');
                    const uniqueId = keyId.split('-')[0];

                    const dataOne = common.getData('hjzdTool') || {};

                    if(dataOne['沟通menu']){ // 存储左侧所有的沟通信息
                        if(!dataOne['沟通menu'].some(itemOneClick=>itemOneClick==uniqueId)) {
                            if(uniqueId.length>3 && Number(uniqueId) ) dataOne['沟通menu'].push(uniqueId)
                        }
                    }else{
                        if(uniqueId.length>3 && Number(uniqueId) ) dataOne['沟通menu']=[uniqueId]
                    }

                    if (dataOne['沟通menu-gerenfenxi'] && dataOne['沟通menu-gerenfenxi'].some(it => it === uniqueId)) {
                        this.handleAlreadyProcessedPerson(geekName.innerText, isArrayEndIndex);
                        // console.log(`今日已对【${geekName.innerText}】进行过存储，一天只分析一次！`)
                        return;
                    }
                    if(dataOne['沟通menu--auto-click_id']){
                        if(dataOne['沟通menu--auto-click_id'].some(it=>it===uniqueId)) {
                            this.handleAlreadyProcessedPerson(geekName.innerText, isArrayEndIndex);
                            // console.log(`今日已对【${geekName.innerText}】进行过存储，一天只分析一次！`)
                            // 不允许点击
                            return 
                        } else{
                            if(uniqueId.length>3 && Number(uniqueId) ) dataOne['沟通menu--auto-click_id'].push(uniqueId)  // 允许
                            // geekItem.click();   // 今日没有存储过 可以--点击当前item对话框
                        }
                    }else{
                        if(uniqueId.length>3 && Number(uniqueId) ) dataOne['沟通menu--auto-click_id']=[uniqueId]
                        // geekItem.click();   // 今日没有存储过 可以--点击当前item对话框
                    }
                    await common.simulateMouseMoveAndClick(geekItem);// 点击展示对应的谈话框
                    common.storeData('hjzdTool', dataOne);
                    await new Promise(resolve => setTimeout(resolve, common.getRandomDelay(4000, 10000)));
                    // 接口判断 是否需要在线简历
                    const params={
                        platformCadidateId: uniqueId,
                        platformId: 84 // 因为后端定义的是整数 'boss'
                    }
                    await common.request('post', urlRootPath+'/candidate/selectCandidateList', JSON.stringify(params))
                        .then(async response => {
                            if(response.result=="success"){
                                const data=JSON.parse(response.data)
                                // 500表示这个人不在数据库中，需要传，200表示人在库里，不需要传在线简历
                                
                                ElMessage.closeAll();
                                ElMessage({
                                    message: `本轮正在进行对【${geekName.innerText}】的信息分析...`,
                                    type: 'warning',
                                    duration: 0,
                                    customClass: 'custom-el-message'
                                });
                                const bossPopupClose = document.querySelector('.dialog-wrap .boss-popup__close');
                                if(bossPopupClose){
                                    // 移除 ka 属性
                                    bossPopupClose.removeAttribute('ka');
                                    bossPopupClose.click()
                                } 

                                if(data.code==500){
                                    // console.log(`【${geekName.innerText}】need----之前没入过库--需要在线简历`);
                                    await this.threeStep(item, uniqueId, isArrayEndIndex);
                                }else if(data.code==200){
                                    // console.log(`【${geekName.innerText}】noNeed----之前入过库--不需要在线简历`);
                                    // 不需要在线简历，只需要对话记录
                                    // 获取要滚动的容器
                                    const conversationMessageElement = document.querySelector('.conversation-message');

                                    if (!conversationMessageElement) {
                                        // console.error('找不到 class="conversation-message" 的元素');
                                        return;
                                    }
                                    // 执行对话框滚动---对话沟通内容+附件简历
                                    await this.scrollContainerToTop(conversationMessageElement,null,'自动进行');
                                    ElMessage.closeAll();  // 清除所有显示的消息
                                    ElMessage({
                                        message: `正在进入下一个人...`,
                                        type: 'warning',
                                        duration: 10000, // 消息3秒后自动关闭
                                        // position: 'top-right', // 设置消息显示的位置为右上角
                                        // offset: 40, // 距离页面顶部 40px
                                        customClass: 'custom-el-message' // 自定义类名
                                    });
                                    
                                }
                            }else{
                                ElMessage.closeAll();  // 清除所有显示的消息
                                ElMessage({
                                    message: '判断是否入库接口报错！',
                                    type: 'error',
                                    duration: 10000, // 消息3秒后自动关闭
                                    customClass: 'custom-el-message' // 自定义类名
                                });
                            }
                        })
                        .catch(error => {
                            console.error(error);
                        });
                    
                },

                async delayAndRefreshPage(delay) {
                    await new Promise(resolve => setTimeout(resolve, delay));
                    if (goutongAutoStop.value) {
                        ElMessage.closeAll();  // 清除所有显示的消息
                        ElMessage({
                            message: `自动分析已停止！`,
                            type: 'warning',
                            duration: 3000, // 消息3秒后自动关闭
                            customClass: 'custom-el-message' // 自定义类名
                        });
                        return
                    }
                    if(!goutongAutoStop.value){
                        common.reloadPage();
                    }
                },

                async delayAndExecute(delay, callback) {
                    await new Promise(resolve => setTimeout(resolve, delay));
                    callback();
                },

                handleAlreadyProcessedPerson(personName, isArrayEndIndex) {
                    // if (isArrayEndIndex) {
                    //     ElMessage.closeAll();
                    //     ElMessage({
                    //         message: `所有数据收集完毕!`,
                    //         type: 'success',
                    //         duration: 3000,
                    //         customClass: 'custom-el-message'
                    //     });
                    // } else {
                        ElMessage.closeAll();
                        ElMessage({
                            message: `今日已经对【${personName}】分析过啦，进入下一个...`,
                            type: 'warning',
                            duration: 0,
                            customClass: 'custom-el-message'
                        });
                    // }
                },
                // 一些列接口请求的函数------
                async threeStep(item, uniqueId,isArrayEndIndex) {
                    return new Promise(async(resolve) => {
                        try {
                            // 获取元素-----
                            const elementText = document.querySelector('.notice-blue-freeze .text')?.innerText.trim();
                            if( elementText=='请注意：该牛人已被系统冻结！'){
                                ElMessage.closeAll();
                                ElMessage({
                                    message: `该牛人已被系统冻结，不进行分析!`,
                                    type: 'error',
                                    duration: 0,
                                    customClass: 'custom-el-message'
                                });
                                resolve()
                                return
                            }
                            // 对话框内容获取
                            // const dialogList=await this.getDialogueFun()
                            // 简历获取 
                            const arrList= [document.querySelector('.btn.resume-btn-online'),document.querySelector('.slide-content-click-content')]
                            // 生成 0 或 1 的随机整数
                            const randomInt = Math.floor(Math.random() * 2); // 两个元素的点击都能获取
                            const clickElement = arrList[randomInt]
                            if(clickElement){
                                setTimeout(async () => {
                                    try {
                                        // clickElement.click() // 点击出这个简历小弹窗
                                        await new Promise((resolveClick) => {
                                            common.simulateMouseMoveAndClick( clickElement,async()=>{
                                                // 监听弹框出来了--且弹框内的标签也出来了
                                                await common.waitForElement('.dialog-wrap.active .resume-box .item-base').then(async res=>{
                                                    
                                                    const dialogElement = document.querySelector('.dialog-wrap.active');
                                                    if(dialogElement){
                                                        // 在线简历信息
                                                        await common.resumeContent(dialogElement,bossInfo).then(info=>{
                                                            setTimeout(async() => {
                                                                const bossPopupClose = document.querySelector('.dialog-wrap .boss-popup__close');
                                                                if(bossPopupClose){
                                                                    // 移除 ka 属性
                                                                    bossPopupClose.removeAttribute('ka');
                                                                    bossPopupClose.click()
                                                                } 

                                                                // 获取要滚动的容器
                                                                const conversationMessageElement = document.querySelector('.conversation-message');

                                                                if (!conversationMessageElement) {
                                                                    resolveClick(); // 先完成内部的操作
                                                                    resolve(); // 然后完成整个操作
                                                                    // console.error('找不到 class="conversation-message" 的元素');
                                                                    return;
                                                                }
                                                                // 执行对话框滚动---对话沟通内容+附件简历
                                                                await this.scrollContainerToTop(conversationMessageElement,info,'自动进行');
                                                                ElMessage.closeAll();  // 清除所有显示的消息
                                                                ElMessage({
                                                                    message: `正在进入下一个人...`,
                                                                    type: 'warning',
                                                                    duration: 10000, // 消息3秒后自动关闭
                                                                    // position: 'top-right', // 设置消息显示的位置为右上角
                                                                    // offset: 40, // 距离页面顶部 40px
                                                                    customClass: 'custom-el-message' // 自定义类名
                                                                });
                                                                resolveClick(); // 先完成内部的操作
                                                                resolve(); // 然后完成整个操作
                                                            }, common.getRandomDelay(5000, 20000)); // 3s--5s间隔内随机
                                                        })
                                                    }else{
                                                        resolveClick(); // 先完成内部的操作
                                                        resolve(); // 然后完成整个操作
                                                    }
                                                }).catch(error => {
                                                    resolveClick(); // 先完成内部的操作
                                                    resolve(); // 然后完成整个操作
                                                });
                                            })
                                        })
                                    } catch (error) {
                                        console.error('Error during simulateMouseMoveAndClick:', error);
                                        resolve();
                                    }
                                    
                                }, common.getRandomDelay(3000, 13000)); // 4s--13s间隔内随机
                                
                                
                            }else{
                                // alert('请先进入到具体到对话框界面中')
                                resolve();
                            }
                        } catch (error) {
                            console.error('Error in threeStep:', error);
                            resolve();
                        }
                    });
                },

                // 定义一个函数用于滚动容器
                async scrollContainerToTop(container,info,isPersonalOrAuto) {
                    let lastMessageCount = container.querySelectorAll('.message-item').length;
                    let newMessageCount;

                    while (true) {
                        // 模拟向上滚动
                        container.scrollTop = 0;

                        // 延迟等待滚动完成和新的消息加载
                        await new Promise(resolve => setTimeout(resolve, 1000));

                        // 获取当前消息的数量
                        newMessageCount = container.querySelectorAll('.message-item').length;

                        // 如果消息数量不再变化，说明已经滚动到顶部或没有新的消息加载
                        if (newMessageCount === lastMessageCount) {
                            // 
                            // 然后获取对话信息内容
                            await this.getDialogueFun().then(async goutongObj=>{
                                console.log('对话传参',  JSON.stringify(goutongObj))
                                await common.request('post', urlRootPath+'/communication/addSaveOrUpdate', JSON.stringify(goutongObj))
                                    .then(response => {
                                        // console.log('当前个人对话记录--存储成功',response,JSON.parse(response.data));
                                        if(response.result=='success'){
                                            const data=JSON.parse(response.data)
                                            if(data.code==200){
                                                console.log(`【${bossInfo.getPageGoutongClickPersoName}】--对话记录--存储成功！`)
                                            }else{
                                                console.log(`【${bossInfo.getPageGoutongClickPersoName}】--对话记录--存储失败！`)
                                                ElMessage.closeAll();  // 清除所有显示的消息
                                                ElMessage({
                                                    message: data.msg,
                                                    type: 'error',
                                                    duration: 10000, // 消息3秒后自动关闭
                                                    customClass: 'custom-el-message' // 自定义类名
                                                });
                                            }
                                            // 
                                            if(isPersonalOrAuto=='自动进行'){

                                            }else if(isPersonalOrAuto=='个人进行'){
                                                autoOrPersonalFinish.value=true // 
                                            }
                                            // 防止漏关
                                            const bossPopupClose = document.querySelector('.dialog-wrap .boss-popup__wrapper .boss-popup__close');
                                            if (bossPopupClose) {
                                                // // 移除 ka 属性
                                                // bossPopupClose.removeAttribute('ka');
                                                // console.log('当前对话内容已提取完成！')
                                                bossPopupClose.click();
                                            }
                                        }else{
                                            ElMessage.closeAll();  // 清除所有显示的消息
                                            ElMessage({
                                                message: '对话保存接口报错！',
                                                type: 'error',
                                                duration: 10000, // 消息3秒后自动关闭
                                                customClass: 'custom-el-message' // 自定义类名
                                            });
                                       }
                                    })
                                    .catch(error => {
                                        console.error(error);
                                    });
                            })
                            // console.log("已滚动到顶部或没有新的消息加载");
                            break;
                        }

                        // 更新上一次的消息数量
                        lastMessageCount = newMessageCount;
                    }
                },
                // 对话内容获取
                async getDialogueFun() {
                    return new Promise(async (resolve, reject) => {
                        
                        const conversationMessageEle = document.querySelector('.chat-conversation .conversation-message');
                        const messageCards = document.querySelectorAll('.message-card-wrap');

                        // 遍历这些元素
                        for (let card of messageCards) {
                            // 找到包含对方想发送附件简历给您的标题
                            const titleElement = card.querySelector('.message-card-top-title');

                            // 确认标题内容为 '对方想发送附件简历给您，您是否同意'
                            if (titleElement && titleElement.textContent.trim() === '对方想发送附件简历给您，您是否同意') {
                                // 选择 card-btn 中的“同意”按钮
                                const agreeButton = card.querySelector('.card-btn:last-child');

                                // 检查“同意”按钮是否不包含 class 'disabled'
                                if (agreeButton && !agreeButton.classList.contains('disabled')) {
                                    // 如果按钮不被禁用，则点击该按钮
                                    agreeButton.click();
                                    // console.log('已点击“同意”按钮');
                                } else {
                                    // console.log('“同意”按钮被禁用');
                                }
                            } else {
                                // console.log('未找到匹配的标题');
                            }
                        }

                        await Vue.nextTick();

                        // 选择所有的 message-item 元素
                        const messageItems = conversationMessageEle.querySelectorAll('.message-item');
                        const dialogList = {
                            ialogueRecord:[], // 沟通记录
                            resumeAttachment: null,  // 附件简历-在线链接
                            resumeAnalysisText: null, // 附件简历-文字内容
                            biographicalNotesName: null, // 附件简历--文件名称
                            platformCandidateId: Number(bossInfo.getPageGoutongClickPersoID) ,  // 当前沟通中的面试者的
                            platformUserId: Number(bossInfo.bossUserId), // 当前面试官的
                            platformId:84, // 因为后端定义的是整数 'boss'
                        };
                        const appointSendingTime= common.getCurrentDateTime()  // 默认指定的发送时间
                        const currentTime = new Date()
                        // 分别找到包含 .figure 或 .item-myself 的 message-item 元素
                        for (let item of messageItems) {
                            let obj = null;
                            // :scope >   直接子标签
                            if (item.querySelector('.item-friend')) {
                                // 面试者-------------------------
                                obj = {
                                    identity: 'Interviewee', // 身份--面试者
                                    shenFen: '面试者',
                                    sendingTime: item.querySelector('.message-time .time')?.innerText.trim() || appointSendingTime, // 能取到时间就取值 取不到就填当前抓取数据的时间
                                    avatarImage: item.querySelector('.item-friend img')?.src, // 头像
                                    content: item.querySelector('.text .message-card-top-title')?.innerText.trim() || item.querySelector('.text span')?.innerHTML || item.querySelector('.text')?.innerText.trim(), // 对方想发送附件简历给您，您是否同意 or 对话内容
                                };
                                if(obj.sendingTime && obj.sendingTime.length === 11) { // 08-05 14:30  就默认为当年
                                    const currentYear = currentTime.getFullYear()
                                    recordDate = `${currentYear}-${obj.sendingTime}`
                                    obj.sendingTime=recordDate
                                }else if( obj.sendingTime && obj.sendingTime.length === 5){ // 09:19
                                    const datePart = appointSendingTime.slice(0, 10) // 当天--就是当前分析的时候所在的年月日了
                                    recordDate = `${datePart} ${obj.sendingTime}`
                                    obj.sendingTime=recordDate
                                }
                                // 查找所有具有 class "card-btn" 的元素
                                const cardButtons = item.querySelectorAll('.message-card-buttons .card-btn');

                                for (let button of cardButtons) {
                                    // 检查按钮的文本内容
                                    if (button.textContent.trim() === '点击预览附件简历') {
                                        const pdfName=item.querySelector('.text .message-card-top-title')?.innerText.trim()
                                        // 找到匹配的按钮----执行点击操作
                                        // button.click();
                                        await new Promise((resolveClick) => {
                                            common.simulateMouseMoveAndClick( button,async()=>{
                                                // 使用通用函数等待弹出框加载
                                                const fjjlxxDialog = await common.waitForElement('.dialog-wrap.active .attachment-view iframe');

                                                

                                                const attachmentResumeInfor = await this.getAttachmentResumeInformation(pdfName) // 获取附件简历信息内容
                                                // console.log('attachmentResumeInfor--附件简历信息',attachmentResumeInfor)
                                                // obj.fjjlxxContent = attachmentResumeInfor; // 附件简历信息
                                                dialogList.resumeAnalysisText=attachmentResumeInfor?.text
                                                dialogList.resumeAttachment=attachmentResumeInfor?.pdfUrl
                                                dialogList.biographicalNotesName= attachmentResumeInfor?.name

                                                
                                                await new Promise(resolve => setTimeout(resolve, common.getRandomDelay(4000, 10000)));
                                                const bossPopupClose = document.querySelector('.dialog-wrap .boss-popup__wrapper .boss-popup__close');
                                                if (bossPopupClose) {
                                                    // // 移除 ka 属性
                                                    // bossPopupClose.removeAttribute('ka');
                                                    // console.log('当前对话内容已提取完成！')
                                                    bossPopupClose.click();
                                                }
                                                resolveClick();  // 确保 resolveClick 只有在所有操作完成后才调用
                                            })

                                        });

                                        
                                        // });
                                        
                                    }
                                }
                                dialogList.ialogueRecord.push(obj);
                                // return
                            }
                            if (item.querySelector('.item-myself')) {
                                // 面试官---------------------------
                                obj = {
                                    identity: 'interviewer', // 身份--面试官
                                    shenFen: '面试官',
                                    sendingTime: item.querySelector('.message-time .time')?.innerText.trim() || appointSendingTime, // 能取到时间就取值 取不到就填当前抓取数据的时间
                                    content: item.querySelector('.item-myself .text span')?.innerText.trim() || item.querySelector('.item-myself .text')?.innerText.trim() || (item.querySelector('.item-system .text .address')?.innerText.trim() + item.querySelector('.item-system .text .map-tips')?.innerText.trim()), // 对话内容
                                };
                                if(obj.sendingTime && obj.sendingTime.length === 11) { // 08-05 14:30  就默认为当年
                                    const currentYear = currentTime.getFullYear()
                                    recordDate = `${currentYear}-${obj.sendingTime}`
                                    obj.sendingTime=recordDate
                                }else if( obj.sendingTime && obj.sendingTime.length === 5){ // 09:19
                                    const datePart = appointSendingTime.slice(0, 10) // 当天--就是当前分析的时候所在的年月日了
                                    recordDate = `${datePart} ${obj.sendingTime}`
                                    obj.sendingTime=recordDate
                                }
                                dialogList.ialogueRecord.push(obj);
                                // return
                            }
                            if(item.querySelector(':scope > .message-time') && item.querySelector(':scope > .item-system')){
                                // 可能是：1、面试官向面试者发送了某些请求操作-求简历...简历请求已发送\
                                // 面试官---------------------------
                                obj = {
                                    identity: 'interviewer', // 身份--面试官
                                    shenFen: '面试官',
                                    sendingTime: item.querySelector('.message-time .time')?.innerText.trim() || appointSendingTime, // 能取到时间就取值 取不到就填当前抓取数据的时间
                                    content: item.querySelector('.item-system .text span')?.innerText.trim(), // 对话内容--
                                };
                                if(obj.sendingTime && obj.sendingTime.length === 11) { // 08-05 14:30  就默认为当年
                                    const currentYear = currentTime.getFullYear()
                                    recordDate = `${currentYear}-${obj.sendingTime}`
                                    obj.sendingTime=recordDate
                                }else if( obj.sendingTime && obj.sendingTime.length === 5){ // 09:19
                                    const datePart = appointSendingTime.slice(0, 10) // 当天--就是当前分析的时候所在的年月日了
                                    recordDate = `${datePart} ${obj.sendingTime}`
                                    obj.sendingTime=recordDate
                                }
                                dialogList.ialogueRecord.push(obj);
                                // return
                            }
                            if(item.querySelector(':scope > .message-time') && item.querySelector(':scope > .item-resume')){
                                // 可能是：1、面试官向面试者发送了某些请求操作-求简历...简历请求已发送\
                                // 面试官---------------------------
                                obj = {
                                    identity: 'unknown',// 'unknown', // 身份--面试官
                                    shenFen: '不知', // '不知'
                                    sendingTime: item.querySelector('.message-time .time')?.innerText.trim() || appointSendingTime, // 能取到时间就取值 取不到就填当前抓取数据的时间
                                    content: item.querySelector('.item-resume .message-card-top-title')?.innerText.trim(), // 对话内容--8月1日 沟通的职位-大数据开发工程师
                                };
                                if(obj.sendingTime && obj.sendingTime.length === 11) { // 08-05 14:30  就默认为当年
                                    const currentYear = currentTime.getFullYear()
                                    recordDate = `${currentYear}-${obj.sendingTime}`
                                    obj.sendingTime=recordDate
                                }else if( obj.sendingTime && obj.sendingTime.length === 5){ // 09:19
                                    const datePart = appointSendingTime.slice(0, 10) // 当天--就是当前分析的时候所在的年月日了
                                    recordDate = `${datePart} ${obj.sendingTime}`
                                    obj.sendingTime=recordDate
                                }
                                dialogList.ialogueRecord.push(obj);
                                // return
                            }

                            // 比如: 对方拒绝向您发送附件简历\xxx的手机号   childElementCount获取直接子级元素的数量
                            if(item.childElementCount === 1 && item.querySelector(':scope > div.item-system.clearfix')){
                                // 面试者-------------------------
                                obj = {
                                    identity: 'Interviewee', // 身份--面试者
                                    shenFen: '面试者',
                                    sendingTime: item.querySelector('.message-time .time')?.innerText.trim() || appointSendingTime, // 能取到时间就取值 取不到就填当前抓取数据的时间
                                    content: item.querySelector('.text .message-card-top-title')?.innerText.trim() || item.querySelector('.item-system .text span')?.innerText.trim()
                                };
                                if(obj.sendingTime && obj.sendingTime.length === 11) { // 08-05 14:30  就默认为当年
                                    const currentYear = currentTime.getFullYear()
                                    recordDate = `${currentYear}-${obj.sendingTime}`
                                    obj.sendingTime=recordDate
                                }else if( obj.sendingTime && obj.sendingTime.length === 5){ // 09:19
                                    const datePart = appointSendingTime.slice(0, 10) // 当天--就是当前分析的时候所在的年月日了
                                    recordDate = `${datePart} ${obj.sendingTime}`
                                    obj.sendingTime=recordDate
                                }
                                dialogList.ialogueRecord.push(obj);
                                // return
                            }
                        }

                        console.log('--沟通记录de传参--对话数组',dialogList)
                        if(messageOption.value) messageOption.value.close(); // 手动关闭消息
                        messageOption.value=ElMessage({
                            message: '分析成功！',
                            type: 'success',
                            duration: 2000, // 设置为 0 表示不自动关闭
                            // position: 'top-right', // 设置消息显示的位置为右上角
                            // offset: 40, // 距离页面顶部 40px
                            customClass: 'custom-el-message' // 自定义类名
                        });
                        const finnallyArr=JSON.parse(JSON.stringify(dialogList))
                        finnallyArr.communicationContent=JSON.stringify(dialogList.ialogueRecord)
                        delete finnallyArr.ialogueRecord
                        // console.log('--沟通记录de传参--',finnallyArr)
                        resolve(finnallyArr);

                    });
                },
                // 附件简历信息获取
                getAttachmentResumeInformation(pdfName) {
                    return new Promise(async (resolve, reject) => {
                        // 
                        let iframe = document.querySelector('.dialog-wrap .attachment-view iframe');
                        if (iframe) {
                            iframe.onload = () => {  // 确保 iframe 内容完全加载后再操作
                                try {
                                    // 
                                    // await common.waitForElement('.recommend-list-wrap .dialog-body')
                                    let iframeDocument = iframe.contentDocument || iframe.contentWindow.document;

                                    common.observeElementOther('.page .textLayer span', (resumeDialogElement) => {
                                        // pdf 滚动到底--把所有页面加载出来
                                        function scrollToBottom(element) {
                                            let previousScrollTop = -1;
                                            const scrollInterval = setInterval(() => {
                                                // 如果已经滚动到尽头，停止滚动
                                                if (element.scrollTop === previousScrollTop) {
                                                    clearInterval(scrollInterval);
                                                    let arrList= iframeDocument.querySelectorAll('.textLayer')
                                                    let fullText = '';
                                                    arrList.forEach(item=>{
                                                        if (item) {
                                                            var spans = item.querySelectorAll('span[role="presentation"]');
                                                            // 遍历每个 span，提取其中的文本内容
                                                            let contentItem=null
                                                            spans.forEach(function(span) {
                                                                contentItem += span.textContent?.trim() + ' ';
                                                            });
                                                            
                                                            // 去除多余的空格
                                                            fullText += contentItem.trim();
                                                            
                                                        } else {
                                                            console.log('未找到 class="textLayer" 的元素');
                                                        }
                                                    })
                                                    common.generateAndUploadPDF( urlRootPath+'/common/upload',iframeDocument,pdfName).then(res => {
                                                        // console.log('附件简历接口返回---',res)
                                                        if(res.code==200){
                                                            resolve({ text: fullText, pdfUrl: res.url,name:res.originalFilename });  // 解析 Promise 并附带 PDF URL
                                                        }else{
                                                            ElMessage.closeAll();  // 清除所有显示的消息
                                                            ElMessage({
                                                                message: res.msg,
                                                                type: 'error',
                                                                duration: 10000, // 消息3秒后自动关闭
                                                                customClass: 'custom-el-message' // 自定义类名
                                                            });
                                                            reject(error);  // 如果 PDF 生成失败，则拒绝 Promise
                                                        }
                                                    }).catch(error => {
                                                        reject(error);  // 如果 PDF 生成失败，则拒绝 Promise
                                                    });
                                                    // resolve(fullText);  // 在 iframe 完全加载并提取文本内容后，解析 Promise
                                                } else {
                                                    previousScrollTop = element.scrollTop;
                                                    element.scrollTop += 100;  // 每次滚动的距离
                                                }
                                            }, 100);  // 每100毫秒滚动一次
                                        }
                                        
                                        const viewerContainer = iframeDocument.getElementById('viewerContainer');
                                        if (viewerContainer) {
                                            scrollToBottom(viewerContainer);
                                        }
                                        
                                    }, iframeDocument);

                                    
                                } catch (error) {
                                    console.error('Failed to access iframe content:', error);
                                }
                            }
                            
                            iframe.onerror = () => {
                                reject(new Error('Failed to load iframe content'));
                            };
                        } else {
                            reject(new Error('iframe not found'));
                        }
                    });
                }, 
                // threeStep  requestInterface
                // 一些列接口请求的函数------  点进详情进行简历+对话等内容获取
                async threeStepDetailFun(type) {
                    return new Promise(async(resolve) => {
                        
                        autoOrPersonalFinish.value=false
                        // 获取元素-----
                        const elementText = document.querySelector('.notice-blue-freeze .text')?.innerText.trim();
                        if( elementText=='请注意：该牛人已被系统冻结！'){
                            ElMessage.closeAll();
                            ElMessage({
                                message: `该牛人已被系统冻结，不进行分析!`,
                                type: 'error',
                                duration: 3000,
                                customClass: 'custom-el-message'
                            });
                            resolve()
                            return
                        }
                        // console.log('进行接口存储')
                        
                        const selectedElement = document.querySelector('.geek-item.selected');
                        // 获取 data-id 属性值
                        const dataId = selectedElement?.getAttribute('data-id');
                        const uniqueId = dataId?.split('-')[0]; // 提取key的值的前半部分

                        // 后续1、2操作之前--如果有附件简历发送，未同意的话点击同意
                        if(type==='需要获取在线简历信息'){
                            // 即需要在线简历信息 也需要对话内容（有附件简历信息的话也需要放置到对话内容里）
                            const dialogElement = document.querySelector('.dialog-wrap.active');
                            if(!dialogElement){
                                // 需要模拟打开在线简历
                                // 简历获取 
                                const arrList= [document.querySelector('.btn.resume-btn-online'),document.querySelector('.slide-content-click-content')]
                                // // 生成 0 或 1 的随机整数
                                const randomInt = Math.floor(Math.random() * 2); // 两个元素的点击都能获取
                                const clickElement = arrList[randomInt]
                                if(clickElement){
                                    // 模拟鼠标移动和点击
                                    setTimeout(()=>{

                                        // clickElement.click() // 点击出这个简历小弹窗

                                        common.simulateMouseMoveAndClick( clickElement,()=>{
                                            // 对当前操作的人进行了存储
                                            const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                                            if(dataOne['沟通menu-gerenfenxi']){
                                                dataOne['沟通menu-gerenfenxi'].push(uniqueId)
                                            }else{
                                                dataOne['沟通menu-gerenfenxi']=[uniqueId]
                                            }
                                            common.storeData('hjzdTool', dataOne); // 存储数据

                                            // 监听弹框出来了
                                            common.waitForElement('.dialog-wrap.active .resume-box .item-base').then(async res=>{
                                                
                                                const dialogElement = document.querySelector('.dialog-wrap.active');
                                                // 在线简历已经被模拟打开了
                                                if(dialogElement){
                                                    await common.resumeContent(dialogElement,bossInfo).then(info=>{
                                                        // console.log('最终的简历信息-----------------',info,dialogElement.querySelector('.name .geek-name').innerText)
                                                        setTimeout(()=>{
                                                            const bossPopupClose = document.querySelector('.dialog-wrap .boss-popup__close');
                                                            if(bossPopupClose){
                                                                // 移除 ka 属性
                                                                bossPopupClose.removeAttribute('ka');
                                                                bossPopupClose.click()
                                                            } 

                                                            // 获取要滚动的容器
                                                            const conversationMessageElement = document.querySelector('.conversation-message');

                                                            if (!conversationMessageElement) {
                                                                console.error('找不到 class="conversation-message" 的元素');
                                                                return;
                                                            }
                                                            // 执行对话框滚动
                                                            this.scrollContainerToTop(conversationMessageElement,info,'个人进行');

                                                            // this.getDialogueFun()
                                                            resolve()
                                                        }, common.getRandomDelay(2000, 5000)); // 3s--5s间隔内随机
                                                    })
                                                }else{
                                                }
                                            }).catch(error => {
                                                console.error('Error caught:', error.message);

                                                // 这个错误处理函数将会被执行
                                                setTimeout(()=>{
                                                    const bossPopupClose = document.querySelector('.dialog-wrap .boss-popup__close');
                                                    if(bossPopupClose){
                                                        // 移除 ka 属性
                                                        bossPopupClose.removeAttribute('ka');
                                                        bossPopupClose.click()
                                                    } 
                                                    resolve()
                                                }, 2000); // 3s--5s间隔内随机

                                                const elementText = document.querySelector('.notice-blue-freeze .text')?.innerText.trim();
                                                if( elementText=='请注意：该牛人已被系统冻结！'){
                                                    ElMessage.closeAll();
                                                    ElMessage({
                                                        message: `该牛人已被系统冻结，不进行分析!`,
                                                        type: 'error',
                                                        duration: 3000,
                                                        customClass: 'custom-el-message'
                                                    });
                                                }
                                            });
                                        })
                                    }, common.getRandomDelay(500, 3000)); // 3s--5s间隔内随机
                                }
                                // alert('需要打开在线简历，目前模拟关闭了')
                            }else{
                                

                                // 对当前操作的人进行了存储
                                const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                                if(uniqueId){
                                    if(dataOne['沟通menu-gerenfenxi']){
                                        dataOne['沟通menu-gerenfenxi'].push(uniqueId)
                                    }else{
                                        dataOne['沟通menu-gerenfenxi']=[uniqueId]
                                    }
                                    common.storeData('hjzdTool', dataOne); // 存储数据
                                }
                                

                                // 在线简历已经被打开了--获取在线简历信息+对话内容
                                await common.resumeContent(dialogElement,bossInfo).then(info=>{
                                    // console.log('最终的简历信息-----------------',info,dialogElement.querySelector('.name .geek-name').innerText)
                                
                                    const bossPopupClose = document.querySelector('.dialog-wrap .boss-popup__close');
                                    if(bossPopupClose){
                                        // 移除 ka 属性
                                        bossPopupClose.removeAttribute('ka');
                                        bossPopupClose.click()
                                    } 

                                    // 获取要滚动的容器
                                    const conversationMessageElement = document.querySelector('.conversation-message');

                                    if (!conversationMessageElement) {
                                        console.error('找不到 class="conversation-message" 的元素');
                                        return;
                                    }
                                    // 执行对话框滚动
                                    this.scrollContainerToTop(conversationMessageElement,info,'个人进行');

                                    // this.getDialogueFun()
                                    resolve()
                                })
                            }
                        }else{

                            // 对当前操作的人进行了存储
                            const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                            if(dataOne['沟通menu-gerenfenxi']){
                                dataOne['沟通menu-gerenfenxi'].push(uniqueId)
                            }else{
                                dataOne['沟通menu-gerenfenxi']=[uniqueId]
                            }
                            common.storeData('hjzdTool', dataOne); // 存储数据

                            // 那就是只需要获取对话信息（有附件简历信息的话也需要放置到对话内容里）
                            // 获取要滚动的容器
                            const conversationMessageElement = document.querySelector('.conversation-message');

                            if (!conversationMessageElement) {
                                console.error('找不到 class="conversation-message" 的元素');
                                return;
                            }
                            // 执行对话框滚动
                            this.scrollContainerToTop(conversationMessageElement,null,'个人进行');
                            resolve()
                        }
                        
                    });
                },

            }


             // 侧边拖拽---小球拖动悬浮样式设置>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            function makeDraggable(element, panel) {
                let offsetY = 0; // 鼠标点击位置与小球顶部的偏移
                let dragStartY = 0; // 鼠标点击的起始位置

                element.onmousedown = (e) => {
                    e.preventDefault();
                    dragStartY = e.clientY; // 获取鼠标初始Y坐标
                    offsetY = dragStartY - element.getBoundingClientRect().top; // 计算偏移
                    document.onmousemove = elementDrag; // 绑定鼠标移动事件
                    document.onmouseup = stopElementDrag; // 绑定鼠标释放事件
                };

                function elementDrag(e) {
                    e.preventDefault();
                    const newY = e.clientY - offsetY; // 计算新位置
                    let newTop = newY;

                    if (newTop < 0) newTop = 0; // 防止拖出顶部
                    if (newTop > window.innerHeight - element.clientHeight) newTop = window.innerHeight - element.clientHeight; // 防止拖出底部

                    element.style.top = newTop + "px";
                    panel.style.top = newTop + "px"; // 同步滑动面板的位置
                }

                function stopElementDrag() {
                    document.onmousemove = null; // 解除鼠标移动事件
                    document.onmouseup = null; // 解除鼠标释放事件
                }
            }

            // // 监听整个文档的变化
            // observer.observe(document.body, { childList: true, subtree: true });
            // 监听页面的刷新 之后的操作>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            window.addEventListener('load', ()=>{
                Vue.nextTick(()=>{
                    const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                    // 页面已刷新并加载完成，执行特定操作
                    if(dataOne['当前menu']==='推荐牛人'){

                    }else if(dataOne['当前menu']==='沟通'){
                        Vue.nextTick(() => {
                            // 如果出现广告弹窗,要关闭
                            common.closeAdvertisementFun()
                            if(dataOne['goutongNeedAutomaticAll']==='需要-因为页面有刷新'){
                                ElMessage.closeAll();  // 清除所有显示的消息
                                ElMessage({
                                    message: '正在进行自动分析，可能需大量时间，请耐心等待....',
                                    type: 'warning',
                                    duration: 0, // 消息3秒后自动关闭
                                    // position: 'top-right', // 设置消息显示的位置为右上角
                                    // offset: 40, // 距离页面顶部 40px
                                    customClass: 'custom-el-message' // 自定义类名
                                });
                                const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                                if(dataOne['goutongMenuClickOperate']=='autoSrcollAnalysis'){
                                    // 如果当前点击的是
                                    gtAutoAllDisabled.value=true
                                    goutongAutoStop.value=false
                                    autoOrPersonalFinish.value=false
                                    dataOne['goutongMenuClickOperate']=''
                                    common.storeData('hjzdTool', dataOne); // 存储数据
                                }
                                setTimeout(() => {
                                    getGoutongFunTwo.secondStep();
                                }, common.getRandomDelay(3000, 7000));
                                
                                dataOne['goutongNeedAutomaticAll']='不需要' 
                                common.storeData('hjzdTool', dataOne); // 存储数据
                            }
                        })
                    }
                })
            });



            // 判断 如果没有获取当前的boss面试官的登录信息时，则需要进行获取---------------------------------------
            function isGetBossInformationFun(type) {
                return new Promise((resolve, reject) => {
                    
                    const storedData=common.getData('hjzdTool') || {}; // 获取数据

                    // 当前boss面试官的登录信息

                    if (storedData.bossUserId && storedData.bossUserName && type !=='同步当前面试官信息') {
                        if(storedData.getPageLoginName != storedData.bossUserName){
                            // 已有信息，但是当前boss账号和tool绑定的人名不一致，需要重新匹配   
                             
                            let navItemPersion = document.querySelector('.nav-item.nav-logout');
                            if(!navItemPersion) return
                            const accountSettingsLink = Array.from(navItemPersion.querySelectorAll('a')).find(el => el.textContent.includes('账号设置'));
                            if (accountSettingsLink) {
                                // 告知页面我是被这样点击的
                                const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                                dataOne['是否是tool进行绑定获取boss信息']='是'
                                common.storeData('hjzdTool', dataOne); // 存储数据
                                // 模拟点击操作
                                // accountSettingsLink.click();
                                common.simulateMouseMoveAndClick( accountSettingsLink,()=>{})
                                // 点击完了-因为下面的代码执行不到，所以走了接口监听，监听获取userId信息的接口‘wapi/zppassport/user/accountStatus’ 进行判断是否是这样形式绑定的吗，如果是的话就跳回到之前的页面
                                // console.log('账号设置链接已被点击');
                            } else {
                                // console.log('没有找到账号设置链接');
                            }
                        }else{
                            // 已有信息，不需要重新获取
                            bossInfo.bossUserId=storedData.bossUserId
                            bossInfo.bossUserName=storedData.bossUserName
                            // storedData.isSynchronousBoss='1' // 是否同步boss面试官的信息了
                            resolve(storedData);  // 直接返回存储的数据
                        }
                    } else {
                        // 没有信息，需要重新获取  or  手动按钮触发进行  boss面试官账号信息同步
                        let navItemPersion = document.querySelector('.nav-item.nav-logout');
                        if (!navItemPersion) {
                            alert('boss请登录面试官账号');
                            reject(new Error('未登录'));
                            return;
                        }
                        if(type !=='同步当前面试官信息'){
                            ElMessage.closeAll();  // 清除所有显示的消息
                            ElMessage({
                                message: "tool未获取到boss面试官信息，现在进行获取",
                                type: 'info',
                                duration: 3000, // 消息3秒后自动关闭
                                // position: 'top-right', // 设置消息显示的位置为右上角
                                // offset: 40, // 距离页面顶部 40px
                                customClass: 'custom-el-message' // 自定义类名
                            });
                        }else{
                            ElMessage.closeAll();  // 清除所有显示的消息
                            ElMessage({
                                message: "正在同步当前面试官信息...",
                                type: 'info',
                                duration: 3000, // 消息3秒后自动关闭
                                // position: 'top-right', // 设置消息显示的位置为右上角
                                // offset: 40, // 距离页面顶部 40px
                                customClass: 'custom-el-message' // 自定义类名
                            });
                        }
                        // let uiDropmenuLabel = navItemPersion.querySelector('.ui-dropmenu-label .label-name');
                        // uiDropmenuLabel.click();  // 虽然能点开页面+网络请求也能请求到接口，但是监听不到接口
                        // let liElement = navItemPersion.querySelector('.ui-dropmenu-list .li:nth-child(2)');
                        // liElement.click()
                        const accountSettingsLink = Array.from(navItemPersion.querySelectorAll('a')).find(el => el.textContent.includes('账号设置'));
                        if (accountSettingsLink) {
                            // 告知页面我是被这样点击的
                            
                            const dataOne=common.getData('hjzdTool') || {}; // 获取数据
                            dataOne['是否是tool进行绑定获取boss信息']='是'
                            common.storeData('hjzdTool', dataOne); // 存储数据
                            // 模拟点击操作
                            // accountSettingsLink.click();
                            common.simulateMouseMoveAndClick( accountSettingsLink,()=>{})
                            // 点击完了-因为下面的代码执行不到，所以走了接口监听，监听获取userId信息的接口‘wapi/zppassport/user/accountStatus’ 进行判断是否是这样形式绑定的吗，如果是的话就跳回到之前的页面
                            // console.log('账号设置链接已被点击');
                        } else {
                            // console.log('没有找到账号设置链接');
                        }
                    }
                });
            }


            // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            Vue.onMounted(() => {
                startObserving(); // 页面元素监听

                // 小球拖动悬浮样式设置--------------------------------------------------
                const ball = document.querySelector('#floating-ball');
                const slidingPanel = document.querySelector('#sliding-panel');

                // 初始化可拖动球
                makeDraggable(ball, slidingPanel);

                // 悬停时显示滑动面板
                ball.onmouseover = () => {
                    slidingPanel.style.right = '44px'; // 显示面板距离球npx
                };

                // 鼠标离开球时隐藏滑动面板
                ball.onmouseout = (e) => {
                    const rect = slidingPanel.getBoundingClientRect();
                    const mouseX = e.clientX;
                    const mouseY = e.clientY;

                    // 检查鼠标是否在滑动面板内
                    if (!(mouseX >= rect.left && mouseX <= rect.right && mouseY >= rect.top && mouseY <= rect.bottom)) {
                        slidingPanel.style.right = '-290px'; // 隐藏面板
                    }
                };

                // 悬停时保持面板可见
                slidingPanel.onmouseover = () => {
                    slidingPanel.style.right = '44px'; // 保持可见
                };
                slidingPanel.onmouseout = () => {
                    slidingPanel.style.right = '-290px'; // 隐藏位置
                };

                // ----------------------------------------------------------------------------------------------------
            });

            return {
                confirmationBtn, // 推荐牛人-all
                positionManBtn1,// 职位管理-
                personalDetailsTJNRBtn, // 推荐牛人--单个
                communicateBtn, // 沟通-all自动分析
                checkGoutonBtn, // 查看
                communicateBtnCurrent, // // 沟通-all自动分析--当前沟通页下的内容开始存储分析
                communicateStopBtn,// // 沟通-all自动分析-stop停止
                sendBtn, // 发送文本
                gtAutoAllDisabled,
                autoOrPersonalFinish,
                goutongAutoStop,
                // personalDetailsGTBtn1,  // 沟通--个人分析
                personalDetailsGTBtn2,  // 沟通--在线简历打开
                gtPersonalDisabled, // 按钮是否禁用
                countdown, // 倒计时秒数
                exitBtn, //退出
                bossIdentityUpdateBtn,  // 测试
                jumpOtherWebpage,
                checked1,
                checked2,
                checked3,
                input1,
                ruleFormRef,
                ruleForm,
                rules,
                submitForm,
                resetForm,
                loginPerson,
                bossInfo,
                activeName,
                handleClick,
            };
        }
    };

    const app = Vue.createApp(App);
    if(ElementPlus){
        app.use(ElementPlus);
    } else {
        console.error("ElementPlus 不存在，无法挂载 Vue 应用");
    }
    if (appElement) {
        app.mount(appElement);
    } else {
        console.error("appElement 不存在，无法挂载 Vue 应用");
    }
}