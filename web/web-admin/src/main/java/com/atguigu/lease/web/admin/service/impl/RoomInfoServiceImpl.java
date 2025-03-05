package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.GraphInfoMapper;
import com.atguigu.lease.web.admin.mapper.RoomInfoMapper;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.graph.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private RoomAttrValueService roomAttrValueService;

    @Autowired
    private RoomFacilityService roomFacilityService;

    @Autowired
    private RoomLabelService roomLabelService;

    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;

    @Autowired
    private RoomLeaseTermService roomLeaseTermService;

    @Override
    public void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo) {
        boolean isUpdate = roomSubmitVo.getId() != null;
        super.saveOrUpdate(roomSubmitVo);

        if(isUpdate){
            //1.删除原有的graphInfoList
            LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
            graphInfoWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphInfoWrapper.eq(GraphInfo::getItemId,roomSubmitVo.getId());
            graphInfoService.remove(graphInfoWrapper);

            //2.删除原有的roomAttrValueList
            LambdaQueryWrapper<RoomAttrValue> roomAttrValueWrapper = new LambdaQueryWrapper<>();
            roomAttrValueWrapper.eq(RoomAttrValue::getRoomId,roomSubmitVo.getId());
            roomAttrValueService.remove(roomAttrValueWrapper);

            //3.删除原有的roomFacilityList
            LambdaQueryWrapper<RoomFacility> roomFacilityWrapper = new LambdaQueryWrapper<>();
            roomFacilityWrapper.eq(RoomFacility::getId,roomSubmitVo.getId());
            roomFacilityService.remove(roomFacilityWrapper);

            //4.删除原有的roomLabelList
            LambdaQueryWrapper<RoomLabel> roomLabelWrapper = new LambdaQueryWrapper<>();
            roomLabelWrapper.eq(RoomLabel::getRoomId,roomSubmitVo.getId());
            roomLabelService.remove(roomLabelWrapper);

            //5.删除原有的paymentTypeList
            LambdaQueryWrapper<RoomPaymentType> paymentTypeWrapper = new LambdaQueryWrapper<>();
            paymentTypeWrapper.eq(RoomPaymentType::getId,roomSubmitVo.getId());
            roomPaymentTypeService.remove(paymentTypeWrapper);

            //6.删除原有的leaseTermList
            LambdaQueryWrapper<RoomLeaseTerm> leaseTermWrapper = new LambdaQueryWrapper<>();
            leaseTermWrapper.eq(RoomLeaseTerm::getId,roomSubmitVo.getId());
            roomLeaseTermService.remove(leaseTermWrapper);

        }

        //1.保存新的graphInfoList
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        List<GraphInfo> graphInfos = new ArrayList<>();
        if(!CollectionUtils.isEmpty(graphVoList)) {
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setItemId(roomSubmitVo.getId());
                graphInfos.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfos);
        }

        //2.保存新的roomAttrValueList
        List<Long> attrValueList = roomSubmitVo.getAttrValueIds();
        if(!CollectionUtils.isEmpty(attrValueList)){
            List<RoomAttrValue> roomAttrValues = new ArrayList<>();
            for (Long value : attrValueList) {
                RoomAttrValue roomAttrValue = new RoomAttrValue();
                roomAttrValue.setRoomId(roomSubmitVo.getId());
                roomAttrValue.setAttrValueId(value);
                roomAttrValues.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(roomAttrValues);
        }
        
        //3.保存新的roomFacilityList
        List<Long> roomFacilityList = roomSubmitVo.getFacilityInfoIds();
        if(!CollectionUtils.isEmpty(roomFacilityList)){
            List<RoomFacility> roomFacilities = new ArrayList<>();
            for (Long facilityId : roomFacilityList) {
                RoomFacility roomFacility = new RoomFacility();
                roomFacility.setFacilityId(facilityId);
                roomFacility.setRoomId(roomSubmitVo.getId());
                roomFacilities.add(roomFacility);
            }
            roomFacilityService.saveBatch(roomFacilities);
        }

        //4.保存新的roomLabelList
        List<Long> roomLabelList = roomSubmitVo.getLabelInfoIds();
        if(!CollectionUtils.isEmpty(roomLabelList)){
            List<RoomLabel> roomLabels = new ArrayList<>();
            for (Long id : roomLabelList) {
                RoomLabel roomLabel = new RoomLabel();
                roomLabel.setRoomId(roomSubmitVo.getId());
                roomLabel.setLabelId(id);
                roomLabels.add(roomLabel);
            }
            roomLabelService.saveBatch(roomLabels);
        }

        //5.保存新的paymentTypeList
        List<Long> paymentTypeList = roomSubmitVo.getPaymentTypeIds();
        if(!CollectionUtils.isEmpty(paymentTypeList)){
            List<RoomPaymentType> paymentTypes = new ArrayList<>();
            for (Long paymentId : paymentTypeList) {
                RoomPaymentType pay = new RoomPaymentType();
                pay.setId(paymentId);
                pay.setRoomId(roomSubmitVo.getId());
                paymentTypes.add(pay);
            }
            roomPaymentTypeService.saveBatch(paymentTypes);
        }

        //6.保存新的leaseTermList
        List<Long> leaseTermList = roomSubmitVo.getLeaseTermIds();
        if(!CollectionUtils.isEmpty(leaseTermList)){
            List<RoomLeaseTerm> roomLeaseTerms = new ArrayList<>();
            for (Long leaseTermId : leaseTermList) {
                RoomLeaseTerm roomLeaseTerm = new RoomLeaseTerm();
                roomLeaseTerm.setLeaseTermId(leaseTermId);
                roomLeaseTerm.setRoomId(roomSubmitVo.getId());
                roomLeaseTerms.add(roomLeaseTerm);
            }
            roomLeaseTermService.saveBatch(roomLeaseTerms);
        }

    }
}




