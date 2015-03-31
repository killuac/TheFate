//
//  BGBallLightning.m
//  FateClient
//
//  Created by Killua Liu on 12/17/13.
//
//  2张手牌当【普通攻击】或【闪避】使用
//  目标玩家需要知道转化后的卡牌是攻击还是闪避，根据不同的卡牌显示不同的文本提示
//  Server will send the parameter "transformedCardId" to target player's device

#import "BGBallLightning.h"
#import "BGGameScene.h"


@implementation BGBallLightning {
    BGHandCard *_transformedCard;
}

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return [_transformedCard checkPlayerEnablement:player];
}

- (NSString *)useTipText
{
    NSInteger transCardId = (self.player.transformedCardId > 0) ? self.player.transformedCardId : self.transformedCardId;
    _transformedCard = [BGHandCard handCardWithCardId:transCardId ofPlayer:self.player];
    return [BGUtil textWith:_useTipText parameter:_transformedCard.cardText];
}

- (NSString *)targetTipText
{
//  In target player's device, the BGBallLightning instance is different with active player's. It's a new instance.
//  So property _transformedCard is nil, need create new one with transformedCardId from server.
    BGPlayer *activePlayer = self.player.game.activePlayer;
    BGHandCard *transformedCard = [BGHandCard handCardWithCardId:activePlayer.transformedCardId ofPlayer:activePlayer];
    
    if (transformedCard.isAttack){
        return [BGUtil textWith:_targetTipText parameters:self.player.game.tipParameters];
    } else {
        return transformedCard.targetTipText;
    }
}

- (NSString *)historyText
{
//  Ditto
    BGPlayer *activePlayer = self.player.game.activePlayer;
    BGHandCard *transformedCard = [BGHandCard handCardWithCardId:activePlayer.transformedCardId ofPlayer:activePlayer];
    
    NSRange range = [_historyText rangeOfString:REPLACE_SIGN options:NSBackwardsSearch];
    return [_historyText stringByReplacingCharactersInRange:range withString:transformedCard.cardText];
}

@end
