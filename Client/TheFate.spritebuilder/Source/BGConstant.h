//
//  BGConstant.h
//  FateClient
//
//  Created by Killua Liu on 12/16/13.
//
//

#ifndef FateClient_BGConstant_h
#define FateClient_BGConstant_h

#define MAX_CONNECTION_ATTEMPTS         3
#define MAX_LOGIN_ATTEMPTS              5

#define COUNT_MAX_HAND_CARD             ((IS_IPHONE5) ? 7.7 : 6)
#define COUNT_MAX_TABLE_CARD            ((COUNT_MAX_HAND_CARD) - 1)
#define COUNT_MAX_DREW_CARD             5
#define COUNT_DEFAULT_OWN_EQUIPMENT     2

#define DURATION_SCENE_TRANSITION       0.3f
#define DURATION_SELECTED_HERO_MOVE     0.5f
#define DURATION_SELECTED_CARD_MOVE     0.2f
#define DURATION_CARD_MOVE              0.8f
#define DURATION_TABLE_CARD_MOVE        0.8f
#define DURATION_PANNED_CARD_MOVE       0.3f
#define DURATION_USED_CARD_FADE_OUT     0.5f
#define DURATION_LINE_PATH_SCALE        0.3f
#define DURATION_CARD_FLIP              0.15f
#define DURATION_CARD_FLIP_INTERVAL     0.05f
#define DURATION_CARD_ANIMATION_SCALE   0.1f
#define DURATION_CARD_SCALE             0.3f
#define DURATION_SELECTED_CARD_SCALE    0.1f
#define DURATION_POPUP_MOVE             0.5f
#define DURATION_LOADING                0.7f

#define DELAY_CARD_SCALE                0.3f
#define DELAY_ANIMATION                 0.08f
#define DELAY_PLAY_SOUND                0.2f
#define DELAY_TABLE_CLEAR               2.0f
#define DELAY_LINE_PATH_DISMISS         2.0f
#define DELAY_BUBBLE_DISMISS            5.0f
#define DELAY_HISTORY_DISMISS           3.0f
#define DELAY_INFORMATION_DISMISS       3.0f
#define DELAY_REMOVE_LOADING            10.0f

#define DURATION_SCROLL                 2.0f
#define DURATION_MINIMUM_PAN_PRESS      0.1f
#define DURATION_MINIMUM_LONG_PRESS     0.5f
#define DURATION_SWIPE_MINIMUM          0.2f
#define DURATION_SWIPE_MAXIMUM          0.5f

#define SCHEDULE_DALAY_TIME             1.0f

#define SCALE_SELECTED_HERO             0.1f
#define SCALE_SELF_PLAYER_ANIMATION     1.0f
#define SCALE_OTHER_PLAYER_ANIMATION    0.4f
#define CARD_DEFAULT_SCALE              1.0f
#define CARD_SCALE_UP                   1.4f
#define CARD_SCALE_DOWN                 0.85f
#define PROGRESS_BAR_SCALEX             0.33f
#define IPHONE5_ADDITIONAL_SCALEX       0.19f

#define PADDING_CANDIDATE_HEROS         5.0f
#define PADDING_COMPARED_CARD           1.0f
#define PADDING_DREW_CARD               0.0f
#define PADDING_ASSIGNED_CARD           1.0f
#define PADDING_SKILL_BUTTON            0.0f
#define PADDING_TWO_BUTTONS             50.0f
#define PADDING_THREE_BUTTONS           20.0f
#define PADDING_SUITS_BUTTON            0.0f
#define PADDING_SEGMENT                 -10.0f

#define SWIPE_FROM_EDGE_PADDING         SCREEN_WIDTH/50
#define SWIPE_MINIMUM_OFFSET            SCREEN_WIDTH/100
#define SWIPE_MINIMUM_MOVEMENT          SCREEN_WIDTH/50

#define PHOTO_COMPRESSION_QUALITY       0.5f
#define PHOTO_MAX_COMPRESSION           0.1f
#define PHOTO_COMPRESSION_DECREMENT     0.1f
#define PHOTO_MAX_SIZE                  (20 * 1024)

#define FONT_BAOLI                      @"STBaoli-SC-Regular"

#define REPLACE_SIGN                    @"&"    // 历史记录文本中的替换符
#define DELIMITER_SIGN                  @"、"    // 多个目标英雄/多张卡牌的分隔符
#define CONNECTOR_SIGN                  @"~"    // 牌局的"回合开始的文本"和"开始时间"的连接符



#define INFO_CONNECTION_FAILED          NSLocalizedString(@"请检查网络连接是否正常", "")
#define INFO_NETWORK_UNSTABLE           NSLocalizedString(@"网络不稳定，请稍后再试。", "")
#define INFO_LOGIN_MANY_TIMES           NSLocalizedString(@"登录太频繁，请稍后再试。", "")
#define INFO_FEEDBACK_SUCCESS           NSLocalizedString(@"感谢您为宿命提供意见或建议!", "")
#define INFO_LEVEL_NOT_ENOUGH           NSLocalizedString(@"该场次需等级达到&级才可进入", "")
#define INFO_COMING_SOON                NSLocalizedString(@"即将开放，敬请期待！", "")
#define INFO_USER_WAS_KICKED            NSLocalizedString(@"玩家【&】被提出了房间", "")
#define INFO_STORE_CONNECTION_FAILED    NSLocalizedString(@"不能连接到商店，请重试一次。", "")
#define INFO_COIN_NOT_ENOUGH            NSLocalizedString(@"金币数量不足，请先购买金币。", "")
#define INFO_BECAME_VIP                 NSLocalizedString(@"恭喜您成为VIP会员！", "")
#define INFO_BOUGHT_HERO                NSLocalizedString(@"您可以在游戏中使用【&】了", "")
#define INFO_HERO_WAS_PICKED            NSLocalizedString(@"【&】已经被其他玩家点选", "")
#define PLACEHOLDER_USER_NAME           NSLocalizedString(@"帐号", "Login username place holder")
#define PLACEHOLDER_PASSWORD            NSLocalizedString(@"密码", "Login password place holder")
#define PLACEHOLDER_NEW_USER_NAME       NSLocalizedString(@"帐号：字母或数字，首位为字母", "Register username place holder")
#define PLACEHOLDER_NEW_PASSWORD        NSLocalizedString(@"密码：长度至少6位(默认和帐号相同)", "Register password place holder")
#define PLACEHOLDER_ROOM_NUMBER         NSLocalizedString(@"请输入要查找的房间号", "")
#define PLACEHOLDER_ROOM_PASSWORD       NSLocalizedString(@"房间密码", "")
#define PLACEHOLDER_FIND_ROOM_PWD       NSLocalizedString(@"房间密码(若没有密码，无需输入)", "")
#define TIP_USER_NOT_FOUND              NSLocalizedString(@"帐号不存在", "")
#define TIP_WRONG_PASSWORD              NSLocalizedString(@"密码错误", "")
#define TIP_USER_NAME_TAKEN             NSLocalizedString(@"帐号名已经被占用", "")
#define TIP_USER_NAME_ILLEGAL           NSLocalizedString(@"帐号名必须由字母或数字组成，首为字母", "")
#define TIP_PASSWORD_LENGTH             NSLocalizedString(@"密码长度不够", "")
#define TIP_ROOM_NOT_FOUND              NSLocalizedString(@"您查找的房间不存在", "")
#define TIP_INPUT_PASSWORD              NSLocalizedString(@"请输入房间密码", "")
#define TIP_WRONG_ROOM_PASSWORD         NSLocalizedString(@"房间密码错误", "")
#define TIP_FEEDBACK_TITLE              NSLocalizedString(@"反馈标题", "")
#define ALERT_LOGOUT                    NSLocalizedString(@"您要返回到登陆界面吗？", "")
#define ALERT_EXIT_GAME                 NSLocalizedString(@"您要退出游戏吗？", "")
#define ALERT_ESCAPE                    NSLocalizedString(@"提前离开会损失经验，确定逃跑吗？", "")
#define ALERT_PURCHASE                  NSLocalizedString(@"您要购买“&”吗？", "")
#define ALERT_PICK_HERO                 NSLocalizedString(@"您要花费&枚金币点选“&”吗？", "")
#define ALERT_BUTTON_OKAY               NSLocalizedString(@"确定", "")
#define ALERT_BUTTON_CANCEL             NSLocalizedString(@"取消", "")

#define COLOR_RED                       NSLocalizedString(@"红色", "")
#define COLOR_BLACK                     NSLocalizedString(@"黑色", "")
#define SUITS_SPADES                    NSLocalizedString(@"黑桃", "")
#define SUITS_HEARTS                    NSLocalizedString(@"红桃", "")
#define SUITS_CLUBS                     NSLocalizedString(@"梅花", "")
#define SUITS_DIAMONDS                  NSLocalizedString(@"方块", "")
#define HERO_NAME_YOU                   NSLocalizedString(@"你", "The displayed hero name is self, so replace by you")
#define HERO_NAME_SELF                  NSLocalizedString(@"自己", "The displayed hero name is self, so replace by self")
#define PLAYER_ALIVE                    NSLocalizedString(@"存活", "Player is alive")
#define PLAYER_DEAD                     NSLocalizedString(@"阵亡", "Player is dead")
#define PLAYER_ESCAPED                  NSLocalizedString(@"逃跑", "Player is escaped")

#endif
