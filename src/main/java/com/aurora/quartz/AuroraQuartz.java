package com.aurora.quartz;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.aurora.constant.ArticleConstant;
import com.aurora.entity.*;
import com.aurora.enums.DeleteStatusEnum;
import com.aurora.juhe.model.dto.JuheNetworkhotResult;
import com.aurora.juhe.model.dto.JuheNetworkhotResultList;
import com.aurora.juhe.model.dto.JuheSoupResult;
import com.aurora.juhe.service.JuheService;
import com.aurora.mapper.UniqueViewMapper;
import com.aurora.mapper.UserAuthMapper;
import com.aurora.model.dto.UserAreaDTO;
import com.aurora.model.vo.ArticleVO;
import com.aurora.service.*;
import com.aurora.util.IpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.aurora.constant.CommonConstant.UNKNOWN;
import static com.aurora.constant.RedisConstant.*;

@Slf4j
@Component("auroraQuartz")
public class AuroraQuartz {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleResourceService roleResourceService;

    @Autowired
    private UniqueViewMapper uniqueViewMapper;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    TalkService talkService;
    public static final String NET_WORK_HOT = "国际热点新闻";

    @Value("${website.url}")
    private String websiteUrl;

    public void saveUniqueView() {
        Long count = redisService.sSize(UNIQUE_VISITOR);
        UniqueView uniqueView = UniqueView.builder()
                .createTime(LocalDateTimeUtil.offset(LocalDateTime.now(), -1, ChronoUnit.DAYS))
                .viewsCount(Optional.of(count.intValue()).orElse(0))
                .build();
        uniqueViewMapper.insert(uniqueView);
    }

    public void clear() {
        redisService.del(UNIQUE_VISITOR);
        redisService.del(VISITOR_AREA);
    }

    public void statisticalUserArea() {
        Map<String, Long> userAreaMap = userAuthMapper.selectList(new LambdaQueryWrapper<UserAuth>().select(UserAuth::getIpSource))
                .stream()
                .map(item -> {
                    if (Objects.nonNull(item) && StringUtils.isNotBlank(item.getIpSource())) {
                        return IpUtil.getIpProvince(item.getIpSource());
                    }
                    return UNKNOWN;
                })
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()));
        List<UserAreaDTO> userAreaList = userAreaMap.entrySet().stream()
                .map(item -> UserAreaDTO.builder()
                        .name(item.getKey())
                        .value(item.getValue())
                        .build())
                .collect(Collectors.toList());
        redisService.set(USER_AREA, JSON.toJSONString(userAreaList));
    }

    public void baiduSeo() {
        List<Integer> ids = articleService.list().stream().map(Article::getId).collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "data.zz.baidu.com");
        headers.add("User-Agent", "curl/7.12.1");
        headers.add("Content-Length", "83");
        headers.add("Content-Type", "text/plain");
        ids.forEach(item -> {
            String url = websiteUrl + "/articles/" + item;
            HttpEntity<String> entity = new HttpEntity<>(url, headers);
            restTemplate.postForObject("https://www.baidu.com", entity, String.class);
        });
    }


    public void clearJobLogs() {
        jobLogService.cleanJobLogs();
    }

    public void importSwagger() {
        resourceService.importSwagger();
        List<Integer> resourceIds = resourceService.list().stream().map(Resource::getId).collect(Collectors.toList());
        List<RoleResource> roleResources = new ArrayList<>();
        for (Integer resourceId : resourceIds) {
            roleResources.add(RoleResource.builder()
                    .roleId(1)
                    .resourceId(resourceId)
                    .build());
        }
        roleResourceService.saveBatch(roleResources);
    }

    @Autowired
    JuheService juheService;
    public static final String ES_SYNC_KEY = "ES_SYNC_KEY";

    public void importDataIntoES() {
        //查询新增的 做增量插入
        Object o = redisService.get(ES_SYNC_KEY);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getIsDelete, DeleteStatusEnum.F.getStatus());
        if (o != null) {
            Long timestamp = (Long) o;
            LocalDateTime time2 = LocalDateTime.ofEpochSecond(timestamp / 1000, 0,
                    ZoneOffset.ofHours(8));
            wrapper.and(wq -> {
                wq.gt(Article::getCreateTime, time2).or().gt(Article::getUpdateTime, time2);
            });
        }
        List<Article> articles = articleService.list(wrapper);
        if (CollectionUtils.isEmpty(articles)) {
            return;
        }
        LocalDateTime localDateTime;
        if (articles.size() > 1) {
            Article article1 = articles.stream().filter(item -> item.getUpdateTime() != null).max(Comparator.comparing(Article::getUpdateTime)).get();
            Article article2 = articles.stream().max(Comparator.comparing(Article::getCreateTime)).get();
            localDateTime = article2.getCreateTime().isAfter(article1.getUpdateTime()) ? article2.getCreateTime() : article1.getUpdateTime();
        } else {
            Article article = articles.get(0);
            localDateTime = articles.get(0).getUpdateTime() == null ? article.getCreateTime() : article.getUpdateTime();
        }
        long milli = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        redisService.set(ES_SYNC_KEY, milli);
//        for (Article article : articles) {
//            elasticsearchMapper.save(BeanCopyUtil.copyObject(article, ArticleSearchDTO.class));
//        }
    }

    /**
     * @description: 获取全球热点
     * @author: jonk
     * @date: 24/5/2023
     * @return:
     */

    @Autowired
    TagService tagService;

    public void everyNetworkhot() {
//        全球新闻
        JuheNetworkhotResult result = juheService.queryNetworkhot();
        List<JuheNetworkhotResultList> list = result.getList();
        StringBuilder sb = new StringBuilder();
        ArticleVO article = new ArticleVO();
        article.setArticleTitle(LocalDate.now() + NET_WORK_HOT);
        list.forEach(item -> {
            String title = item.getTitle();
            String digest = item.getDigest();
            Number hotnum = item.getHotnum();
            sb.append("##### ").append(title).append("(" + hotnum + ")").append("\n");
            if (digest.length() > 60) {
                sb.append("      ").append(digest, 0, 60).append("\n");
                sb.append("      ").append(digest.substring(60)).append("\n");
            } else {
                sb.append("      ").append(digest).append("\n");
            }
        });
        article.setArticleContent(sb.toString());
        article.setArticleCover(ArticleConstant.NET_WORK_HOT_PIC_URL);
        article.setCategoryName(ArticleConstant.NET_WORK_HOT_CATEGORY_NAME);
        List<String> arr = new ArrayList<>();
        for (String s : ArticleConstant.NET_WORK_HOT_TAG_NAME.split(",")) {
            arr.add(s);
        }
        article.setTagNames(arr);
        article.setUserId(ArticleConstant.NET_WORK_HOT_USER_ID);
        article.setStatus(ArticleConstant.NET_WORK_HOT_STATUS);
        articleService.saveOrUpdateArticle(article);
    }


    /**
     * @description: 每日一句
     * @author: jonk
     * @date: 25/5/2023
     * @return:
     */
    public void everyDaySoup() {
        JuheSoupResult soup = juheService.soup();
        Talk talk = new Talk();
        talk.setUserId(ArticleConstant.NET_WORK_HOT_USER_ID);
        talk.setContent(soup.getText());
        talk.setCreateTime(LocalDateTime.now());
        talkService.save(talk);
    }




    @Autowired
    UserInfoService userInfoService;
    //    int corePoolSize,
//    int maximumPoolSize,
//    long keepAliveTime,
//    TimeUnit unit,
//    BlockingQueue<Runnable> workQueue,
//    ThreadFactory threadFactory,
//    RejectedExecutionHandler handler;
    private int corePoolSize = 8;
    private int maximumPoolSize = 32;
    private long keepAliveTime = 30;
    private TimeUnit unit = TimeUnit.SECONDS;
    private BlockingQueue workQueue = new ArrayBlockingQueue(100);

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);

    //批量数据更新  模拟 大用户量下给用户新增拼音
    public void userNameAddPy() {

        //获取区间用户
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(UserInfo::getId).orderByDesc(UserInfo::getId).last("limit 1");
        Integer maxId = userInfoService.getOne(wrapper).getId();

        LambdaQueryWrapper<UserInfo> wrapper2 = new LambdaQueryWrapper<>();
        wrapper.select(UserInfo::getId).orderByAsc(UserInfo::getId).last("limit 1");
        Integer minId = userInfoService.getOne(wrapper2).getId();
        //用户分批进入多线程处理
        int index = minId;
        for (int i = index; i <= maxId; i += 50) {
            int finalIndex = index;
//            threadPoolExecutor.submit(()->{
//                List<UserInfo> list = userInfoService.list(Wrappers.lambdaQuery(UserInfo.class).lt(UserInfo::getId, finalIndex+50).gt(UserInfo::getId, finalIndex));
//                //do
//                log.info("处理数据条数 {} ",list.size());
//            });
            Future submit = threadPoolExecutor.submit(new userNameTopyTask(finalIndex));

            index += 50;
        }

    }

    class userNameTopyTask implements Callable {

        private int finalIndex;

        public userNameTopyTask(int finalIndex) {
            this.finalIndex = finalIndex;
        }

        @Override
        public Object call() throws Exception {
            List<UserInfo> list = userInfoService.list(Wrappers.lambdaQuery(UserInfo.class).le(UserInfo::getId, finalIndex + 50).gt(UserInfo::getId, finalIndex));
            //do
            log.info("处理数据条数 {} ", list.size());
            return list.size();
        }
    }
}
