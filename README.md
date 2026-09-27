# Replication Extended

由 cyhddyx 制作的 [Replication](https://www.curseforge.com/minecraft/mc-mods/replication) 非官方扩展模组。

添加八种专用物质储罐，每个储罐固定存储一种物质。即使物质被完全消耗，储罐也不会接受其他类型的物质，方便搭建稳定的自动化物质存储网络。每种专用储罐提供基础与 4X 两种容量档位，4X 储罐的容量为基础储罐的四倍。

## 功能

- 八种专用储罐：大地、下界、有机、末影、金属、珍宝、生命、量子。
- 两种容量档位：基础储罐与 4X 储罐，4X 容量为基础的四倍。
- 物质类型固定，空罐也保持锁定。
- 不同物质采用对应颜色的贴图，透过观察窗可查看实时储量变化。
- 支持从顶部和底部连接 Replication 物质网络。
- 可在储罐界面中设置网络优先级。
- 打掉储罐后保留其中的物质，重新放置即可继续使用。
- 支持物品储量提示和 Jade 信息显示。
- 支持简体中文和英文。

## 运行环境与前置

- Minecraft 1.21.1 / NeoForge 21.1.250+
- [Replication](https://www.curseforge.com/minecraft/mc-mods/replication)
- [Titanium](https://www.curseforge.com/minecraft/mc-mods/titanium)
- 可选：[Jade](https://www.curseforge.com/minecraft/mc-mods/jade)

客户端和服务端均需安装。

## 合成

### 基础储罐

将原版 Replication 物质储罐放在工作台中央，在上下左右放置四个对应材料，即可合成专用储罐。

**合成前请清空原储罐，当前配方不会转移其中存储的物质。**

| 物质类型 | 合成材料 |
| --- | --- |
| 大地 | 泥土 |
| 下界 | 下界岩 |
| 有机 | 骨粉 |
| 末影 | 末影珍珠 |
| 金属 | 铁锭 |
| 珍宝 | 金锭 |
| 生命 | 黏液球 |
| 量子 | 紫水晶碎片 |

### 4X 储罐

将同类型的专用储罐放在工作台中央，在上下左右放置四个复制物质锭，即可将其升级为 4X 储罐：

```
 I
ITI
 I
```

- `I`：复制物质锭
- `T`：同类型的专用储罐

八种物质均适用，例如「大地物质储罐」会升级为「4X 大地物质储罐」。4X 储罐的容量是基础储罐的四倍，基础容量由 `ReplicationConfig.MatterTank.CAPACITY` 决定。

**升级不会转移储罐中存储的物质，请先清空。**

## 反馈

[提交问题或功能建议](https://github.com/cyhddyx/ReplicationExtended/issues)。

## 致谢

感谢 Buuz135 及 Replication 的贡献者。本扩展使用了原模组的部分储罐模型、贴图与代码，相关许可见[第三方许可声明](THIRD_PARTY_NOTICES.txt)。
