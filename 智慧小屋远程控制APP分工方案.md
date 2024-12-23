# 智慧小屋远程控制APP分工方案

## 成员1: MQTT通信与核心功能

### 1. MQTT通信模块
- MqttService.kt
  - MQTT服务连接与初始化
  - 消息发布与订阅
  - 警报处理
  - 网络状态监测
- RequestParam.kt
  - 请求参数封装
- ResponseParam.kt 
  - 响应数据解析

### 2. 登录
- HouseActivity.kt
  - 登录界面
  - 自动登录功能
  - 小屋编号验证
- SplashActivity.kt
  - 启动页面

### 3. 客厅与卧室模块
- LivingFragment.kt
  - 客厅灯光控制(开关、颜色、模式)
  - 房梁灯控制
  - 入户门控制
  - 噪音数据显示
  - 天气显示
- LivingViewModel.kt
  - 客厅数据管理
- BedFragment.kt
  - 卧室灯光控制
  - 环境数据显示(温度、湿度、光照、气压)
- BedViewModel.kt
  - 卧室数据管理

## 成员2: UI与扩展功能

### 1. 主框架
- MainActivity.kt
  - 底部导航栏
  - Fragment切换
  - 退出功能
### 2. 厨房模块
- KitchenFragment.kt
  - 厨房灯光控制
  - 抽油烟机控制
  - 火焰报警器控制
  - 燃气报警器控制
  - CO浓度显示
- KitchenViewModel.kt
  - 厨房数据管理

### 3. 浴室模块
- BathFragment.kt
  - 浴室灯光控制
  - 热水器控制
  - 浴霸控制
  - 排气扇控制(三档速度)
  - 窗户控制
  - 窗帘控制

### 4. 关于与场景模块
- AboutFragment.kt
  - 离家模式(一键关闭设备)
  - 回家模式(一键开启设备)
  - 设备选择对话框
  - 进度显示对话框(CustomDialog.kt)

### 5. UI美化
- 布局文件优化
- 主题样式调整
- 动画效果
- 用户体验改进
