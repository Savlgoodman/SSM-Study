package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.RoomInfo;
import com.atguigu.lease.model.enums.ReleaseStatus;
import com.atguigu.lease.web.admin.service.RoomInfoService;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "房间信息管理")
@RestController
@RequestMapping("/admin/room")
public class RoomController {

    @Autowired
    RoomInfoService service;

    @Operation(summary = "保存或更新房间信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody RoomSubmitVo roomSubmitVo) {
        service.saveOrUpdateRoom(roomSubmitVo);
        return Result.ok();
    }

    @Operation(summary = "根据条件分页查询房间列表")
    @GetMapping("pageItem")
    public Result<IPage<RoomItemVo>> pageItem(@RequestParam long current, @RequestParam long size, RoomQueryVo queryVo) {
        IPage<RoomItemVo> page = new Page<>(current, size);
        IPage<RoomItemVo> res = service.pageRoomItem(page, queryVo);
        return Result.ok(res);
    }

    @Operation(summary = "根据id获取房间详细信息")
    @GetMapping("getDetailById")
    public Result<RoomDetailVo> getDetailById(@RequestParam Long id) {
        RoomDetailVo roomDetailVo = service.getDetailById(id);
        return Result.ok(roomDetailVo);
    }

    @Operation(summary = "根据id删除房间信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        service.removeRoomById(id);
        return Result.ok();
    }

    @Operation(summary = "根据id修改房间发布状态")
    @PostMapping("updateReleaseStatusById")
    public Result updateReleaseStatusById(Long id, ReleaseStatus status) {
        LambdaUpdateWrapper<RoomInfo> roomInfoWrapper = new LambdaUpdateWrapper<>();
        roomInfoWrapper.eq(RoomInfo::getId, id);
        roomInfoWrapper.set(RoomInfo::getIsRelease, status);
        service.update(roomInfoWrapper);
        return Result.ok();
    }

    @GetMapping("listBasicByApartmentId")
    @Operation(summary = "根据公寓id查询房间列表")
    public Result<List<RoomInfo>> listBasicByApartmentId(Long id) {
        LambdaQueryWrapper<RoomInfo> roomInfoWrapper = new LambdaQueryWrapper<>();
        roomInfoWrapper.eq(RoomInfo::getApartmentId, id);
        roomInfoWrapper.eq(RoomInfo::getIsRelease, ReleaseStatus.RELEASED);
        List<RoomInfo> res = service.list(roomInfoWrapper);
        return Result.ok(res);
    }

}


















