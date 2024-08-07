package com.gxy.service.impl;

import com.gxy.entity.Candidate;
import com.gxy.mapper.postgresql.CandidateMapper;
import com.gxy.service.CandidateService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author Gxy
 * @since 1.0.0
 */
@Service
public class CandidateServiceImpl extends ServiceImpl<CandidateMapper, Candidate> implements CandidateService {

}
