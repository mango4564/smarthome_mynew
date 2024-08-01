package com.ichippower.smarthousedemo.mqtt

/**
 * houseNumber: 智慧小屋编号
 * commandType: 命令类型："data"：获取数据（device，switch，color和mode无效），"mode"：切换模式
 * device：设备。客厅：门，灯，灯带；卧室：灯；厨房：灯，抽油烟机，火焰报警，燃气报警；浴室：浴霸，热水器，排气扇，灯，窗户，窗帘，自动关闭窗帘，自动打开窗帘
 *       （例：客厅的灯，如果设备的类型只有一个，省略房间）
 * switch：on/off，切换开关（color和mode无效）
 * color：yellow/blue/red/purple，客厅的灯切换颜色，其他设备无效，单独修改color，mode要设为""
 * mode：模式一/模式二/模式三，客厅的灯切换模式，其他设备无效，单独修改mode，color要设为""
 */
class RequestParam(
    private var houseNumber: String,
    private var commandType: String,
    private var device: String,
    private var switch: String,
    private var color: String,
    private var mode: String
)