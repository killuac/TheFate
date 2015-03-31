//
//  BGCard.h
//  FateClient
//
//  Created by Killua Liu on 6/30/13.
//
//

#import <Foundation/Foundation.h>
#import "BGUtil.h"
#import "BGMacro.h"
#import "BGFileConstant.h"

#define kCardEnum           @"cardEnum"
#define kCardName           @"cardName"
#define kCardText           @"cardText"
#define kCardDesc           @"cardDesc"

@interface BGCard : NSObject
{
    NSInteger _cardId;
    NSString *_cardName;
    NSString *_cardText;
    NSString *_cardDesc;
}

@property (nonatomic, readonly) NSInteger cardId;
@property (nonatomic, copy, readonly) NSString *cardName;
@property (nonatomic, copy, readonly) NSString *cardText;
@property (nonatomic, copy, readonly) NSString *cardDesc;

@property (nonatomic, copy, readonly) NSString *imageName;
@property (nonatomic, copy, readonly) NSString *avatarImageName;
@property (nonatomic, copy, readonly) NSString *avatarFontName;     // Getting according to diff card name character count
@property (nonatomic, copy, readonly) NSString *verticalText;       // Make character vertical for display

- (id)initWithCardId:(NSInteger)cardId;
+ (id)cardWithCardId:(NSInteger)cardId;

- (BOOL)isPlayingCard;

@end
