//
//  BGElunesArrow.m
//  FateClient
//
//  Created by Killua Liu on 12/10/13.
//
//

#import "BGElunesArrow.h"
#import "BGGameScene.h"

@implementation BGElunesArrow

- (NSString *)targetTipText
{
    NSString *text = (self.player.isStrengthened) ?
        [BGPlayingCard suitsTextBySuits:self.player.selectedSuits] :
        [BGPlayingCard colorTextByColor:self.player.selectedColor];
    NSArray *parameters = @[self.player.game.tipParameters.firstObject, text];
    return [BGUtil textWith:_targetTipText parameters:parameters];
}

@end
