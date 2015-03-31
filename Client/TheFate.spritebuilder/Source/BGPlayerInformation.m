//
//  BGPlayerInformation.m
//  TheFate
//
//  Created by Killua Liu on 3/23/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPlayerInformation.h"
#import "BGGameScene.h"

@implementation BGPlayerInformation

- (void)didLoadFromCCB
{
    [self setTitleLabel];
    
//    _textView = [[BGTextView alloc] initWithFrame:CGRectZero];
//    _textView.font = [UIFont systemFontOfSize:[self fontSize]];
//    [self setUITextViewFrame];
//    [[CCDirector sharedDirector].view addSubview:_textView];
    
    BGGameScene *game = [self runningSceneNode];
    _leftArrow.visible = _rightArrow.visible = (GameStateHeroChoosing != game.state);
}

//- (CGFloat)fontSize
//{
//    return (IS_PHONE) ? 11 : 22;
//}

- (CGSize)heroInfoBgSize
{
    CGFloat width = _heroInfoFrame.contentSize.width;
    CGFloat height = _heroInfoFrame.contentSize.height;
    return CGSizeMake(width*_heroInfoFrame.scaleX, height*_heroInfoFrame.scaleY);
}

//- (void)setUITextViewFrame
//{
////  PlayerInformation node(position and size) is designed according to iPad size(512*384)
////  So need adjust its position and size in actual device(iPhone) because of UITextView frame's issue
//    CGPoint glPos = ccp(SCREEN_WIDTH*_heroInfoFrame.position.x, SCREEN_HEIGHT*_heroInfoFrame.position.y);
//    glPos = ccp(glPos.x-self.heroInfoBgSize.width*0.48f, glPos.y);
//    CGPoint origin = [[CCDirector sharedDirector] convertToUI:glPos];
//    
//    CGSize size = CGSizeZero;
//    size.width = SCALE_MULTIPLIER * self.heroInfoBgSize.width * 0.98f;
//    size.height = SCALE_MULTIPLIER * self.heroInfoBgSize.height * 0.98f;
//    
//    CGRect frame = CGRectZero;
//    frame.origin = origin;
//    frame.size = size;
//    _textView.frame = frame;
//}

- (void)setPlayer:(BGPlayer *)player
{
    _player = player;
    
    EsUser *user = [[BGClient sharedClient] userByUserName:player.playerName];
    if (user) {     // May be AI player
        _nickNameLabel.string = user.nickName;
        _levelLabel.string = @(user.level).stringValue;
        _winRateLabel.string = [NSString stringWithFormat:@"%.1f%%", user.winRate];
        _escapeRateLabel.string = [NSString stringWithFormat:@"%.1f%%", user.escapeRate];
    } else {
        _nickNameLabel.string = _player.playerName;
    }
    
    BGHeroCard *heroCard = _player.character.heroCard;
    _heroAvatar.spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:heroCard.avatarImageName];
    
    _heroNameLabel.fntFile = heroCard.avatarFontName;
    _heroNameLabel.string = heroCard.verticalText;
    _heroNameLabel.scale = 1 / (FONT_SCALE_FACTOR);
    
    CCSpriteFrameCache *frameCache = [CCSpriteFrameCache sharedSpriteFrameCache];
    _handCardCountFrame.spriteFrame = [frameCache spriteFrameByName:_player.character.heroCard.attrImageName];
    _handCardCountLabel.string = @(_player.character.heroCard.handSizeLimit).stringValue;
    
    [self setRoleMark];
    [self setHeroDesc:heroCard.cardDesc];
    
//  Don't need mark self and next player's role
    _roleButton.enabled = (player.game.isRoleMode && !_player.isMe && ![_player isEqual:_player.game.nextPlayer]);
}

- (void)setRoleMark
{
    NSString *roleImage = (_player.roleCard) ? _player.roleCard.imageName : kImageRoleUnknow;
    CCSpriteFrame *spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:roleImage];
    [_roleButton setBackgroundSpriteFrame:spriteFrame forState:CCControlStateNormal];
}

- (void)setHeroDesc:(NSString *)heroDesc
{
    _textLabel.string = [heroDesc stringByReplacingOccurrencesOfString:@"]: " withString:@"]:\n"];
    _textLabel.string = [_textLabel.string stringByReplacingOccurrencesOfString:@"\n+" withString:@"\n\n"];
    
    [_textLabel setTextColor:DESCRIPTION_TEXT_COLOR];
}

- (void)dismiss
{
    [self.delegate didDismissPopup:self];
    [self removeFromParent];
}

- (void)removeFromParent
{
    [super removeFromParent];
//    [_textView removeFromSuperview];
    [self.runningSceneNode setArrowVisible:YES];
}

- (void)showAllRolesMark:(CCButton *)sender
{
    _roleButton.visible = NO;
    _roleBox.visible = YES;
}

- (void)markRole:(CCButton *)sender
{
    _roleButton.visible = YES;
    _roleBox.visible = NO;
    [_player renderRoleWithRoleId:[sender.name integerValue] animated:NO];
    [self setRoleMark];
}

- (void)leftArrowTouched:(CCButton *)sender
{
    _roleBox.visible = NO;
    _roleButton.visible = YES;
    
    NSInteger index = _player.seatIndex - 1;
    if (index < 0) index = _player.game.alivePlayerCount - 1;
    self.player = [_player.game.alivePlayers objectAtIndex:index];
}

- (void)rightArrowTouched:(CCButton *)sender
{
    _roleBox.visible = NO;
    _roleButton.visible = YES;
    
    NSInteger index = _player.seatIndex + 1;
    if (index == _player.game.alivePlayerCount) index = 0;
    self.player = [_player.game.alivePlayers objectAtIndex:index];
}

@end
