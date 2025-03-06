package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.model.enums.ReleaseStatus;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.graph.Graph;
import org.apache.coyote.http11.upgrade.UpgradeServletOutputStream;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private AttrValueMapper attrValueMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Override
    public void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo) {
        boolean isUpdate = roomSubmitVo.getId() != null;
        super.saveOrUpdate(roomSubmitVo);

        if (isUpdate) {
            //1.删除原有的graphInfoList
            LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
            graphInfoWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphInfoWrapper.eq(GraphInfo::getItemId, roomSubmitVo.getId());
            graphInfoService.remove(graphInfoWrapper);

            //2.删除原有的roomAttrValueList
            LambdaQueryWrapper<RoomAttrValue> roomAttrValueWrapper = new LambdaQueryWrapper<>();
            roomAttrValueWrapper.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
            roomAttrValueService.remove(roomAttrValueWrapper);

            //3.删除原有的roomFacilityList
            LambdaQueryWrapper<RoomFacility> roomFacilityWrapper = new LambdaQueryWrapper<>();
            roomFacilityWrapper.eq(RoomFacility::getId, roomSubmitVo.getId());
            roomFacilityService.remove(roomFacilityWrapper);

            //4.删除原有的roomLabelList
            LambdaQueryWrapper<RoomLabel> roomLabelWrapper = new LambdaQueryWrapper<>();
            roomLabelWrapper.eq(RoomLabel::getRoomId, roomSubmitVo.getId());
            roomLabelService.remove(roomLabelWrapper);

            //5.删除原有的paymentTypeList
            LambdaQueryWrapper<RoomPaymentType> paymentTypeWrapper = new LambdaQueryWrapper<>();
            paymentTypeWrapper.eq(RoomPaymentType::getId, roomSubmitVo.getId());
            roomPaymentTypeService.remove(paymentTypeWrapper);

            //6.删除原有的leaseTermList
            LambdaQueryWrapper<RoomLeaseTerm> leaseTermWrapper = new LambdaQueryWrapper<>();
            leaseTermWrapper.eq(RoomLeaseTerm::getId, roomSubmitVo.getId());
            roomLeaseTermService.remove(leaseTermWrapper);

        }

        //1.保存新的graphInfoList
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        List<GraphInfo> graphInfos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(graphVoList)) {
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
        if (!CollectionUtils.isEmpty(attrValueList)) {
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
        if (!CollectionUtils.isEmpty(roomFacilityList)) {
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
        if (!CollectionUtils.isEmpty(roomLabelList)) {
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
        if (!CollectionUtils.isEmpty(paymentTypeList)) {
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
        if (!CollectionUtils.isEmpty(leaseTermList)) {
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

    @Override
    public IPage<RoomItemVo> pageRoomItem(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
        return roomInfoMapper.pageRoomItem(page, queryVo);
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        //1.查询房间信息
        RoomInfo roomInfo = roomInfoMapper.selectById(id);

        //2.查询所属公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(roomInfo.getApartmentId());

        //3.查询图片列表
        List<GraphVo> graphList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, id);

        //4.属性信息列表
        List<AttrValueVo> attrValueVoList = attrValueMapper.selectListByRoomId(id);

        //5.配套信息列表
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByRoomId(id);

        //6.查询标签信息列表
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByRoomId(id);

        //7.查询支付方式列表
        List<PaymentType> paymentTypeList = paymentTypeMapper.selectListByRoomId(id);

        //8.查询可选租期列表
        List<LeaseTerm> leaseTermList = leaseTermMapper.selectListByRoomId(id);

        RoomDetailVo roomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo, roomDetailVo);
        roomDetailVo.setApartmentInfo(apartmentInfo);
        roomDetailVo.setGraphVoList(graphList);
        roomDetailVo.setAttrValueVoList(attrValueVoList);
        roomDetailVo.setFacilityInfoList(facilityInfoList);
        roomDetailVo.setLabelInfoList(labelInfoList);
        roomDetailVo.setPaymentTypeList(paymentTypeList);
        roomDetailVo.setLeaseTermList(leaseTermList);
        return roomDetailVo;
    }

    @Override
    public void removeRoomById(Long id) {

        super.removeById(id);

        //1.删除原有的graphInfoList
        LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
        graphInfoWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
        graphInfoWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphInfoWrapper);

        //2.删除原有的roomAttrValueList
        LambdaQueryWrapper<RoomAttrValue> roomAttrValueWrapper = new LambdaQueryWrapper<>();
        roomAttrValueWrapper.eq(RoomAttrValue::getRoomId, id);
        roomAttrValueService.remove(roomAttrValueWrapper);

        //3.删除原有的roomFacilityList
        LambdaQueryWrapper<RoomFacility> roomFacilityWrapper = new LambdaQueryWrapper<>();
        roomFacilityWrapper.eq(RoomFacility::getId, id);
        roomFacilityService.remove(roomFacilityWrapper);

        //4.删除原有的roomLabelList
        LambdaQueryWrapper<RoomLabel> roomLabelWrapper = new LambdaQueryWrapper<>();
        roomLabelWrapper.eq(RoomLabel::getRoomId, id);
        roomLabelService.remove(roomLabelWrapper);

        //5.删除原有的paymentTypeList
        LambdaQueryWrapper<RoomPaymentType> paymentTypeWrapper = new LambdaQueryWrapper<>();
        paymentTypeWrapper.eq(RoomPaymentType::getId, id);
        roomPaymentTypeService.remove(paymentTypeWrapper);

        //6.删除原有的leaseTermList
        LambdaQueryWrapper<RoomLeaseTerm> leaseTermWrapper = new LambdaQueryWrapper<>();
        leaseTermWrapper.eq(RoomLeaseTerm::getId, id);
        roomLeaseTermService.remove(leaseTermWrapper);
    }


}




