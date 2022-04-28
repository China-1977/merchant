package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.QScore;
import work.onss.domain.Score;
import work.onss.domain.ScoreRepository;

/**
 * 订单管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class ScoreController {
    @Autowired
    private ScoreRepository scoreRepository;

    /**
     * @param id       最后一个ID
     * @param pageable 分页参数
     * @return 订单分页
     */
    @GetMapping(value = {"scores"}, name = "订单列表")
    public Page<Score> scores(@RequestParam Long id, @PageableDefault(sort = {"id"}) Pageable pageable) {
        QScore qScore = QScore.score;
        return scoreRepository.findAll(qScore.id.gt(id), pageable);
    }

}

