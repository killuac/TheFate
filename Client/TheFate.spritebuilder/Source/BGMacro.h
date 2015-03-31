//
//  BGMacro.h
//  FateClient
//
//  Created by Killua Liu on 6/29/13.
//
//

#ifndef FateClient_BGMacro_h
#define FateClient_BGMacro_h

#define SCREEN_SIZE                     [CCDirector sharedDirector].designSize
#define SCREEN_WIDTH                    SCREEN_SIZE.width
#define SCREEN_HEIGHT                   SCREEN_SIZE.height
#define SCREEN_CENTER                   ccp(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2)

#define CONTENT_SCALE_FACTOR            [CCDirector sharedDirector].contentScaleFactor
#define SCALE_MULTIPLIER                CONTENT_SCALE_FACTOR/[CCDirector sharedDirector].UIScaleFactor
#define FONT_SCALE_FACTOR               (IS_IPAD_RETINA) ? CONTENT_SCALE_FACTOR : CONTENT_SCALE_FACTOR*2

#define IS_IPAD_RETINA                  (CCDeviceiPadRetinaDisplay == [CCConfiguration sharedConfiguration].runningDevice)
#define IS_IPHONE5                      (CCDeviceiPhone5RetinaDisplay == [CCConfiguration sharedConfiguration].runningDevice)
#define IS_PAD                          (CCDeviceiPad == [CCConfiguration sharedConfiguration].runningDevice || IS_IPAD_RETINA)
#define IS_PHONE                        ([CCConfiguration sharedConfiguration].runningDevice >= CCDeviceiPhone && \
                                         [CCConfiguration sharedConfiguration].runningDevice < CCDeviceiPad)

#define PLAYING_CARD_SIZE               [[CCSprite spriteWithImageNamed:kImagePlayingCardBack] contentSize]
#define PLAYING_CARD_WIDTH              PLAYING_CARD_SIZE.width
#define PLAYING_CARD_HEIGHT             PLAYING_CARD_SIZE.height

#define TABLE_AREA_CENTER               ccp(SCREEN_WIDTH / 2, SCREEN_HEIGHT * 0.60)
#define ASSIGNING_POPUP_POSITION        ccp(SCREEN_WIDTH / 2, SCREEN_HEIGHT * 0.65)
#define CANDIDATE_HERO_POSITION         ccp(SCREEN_WIDTH / 2, SCREEN_HEIGHT * 0.52)
#define GAME_HISTORY_POSITION           ccp(SCREEN_WIDTH / 2, SCREEN_HEIGHT * 0.75)
#define PROGRESS_BAR_POSITION           ccp(SCREEN_WIDTH * 0.532, SCREEN_HEIGHT * 0.252)

#define NORMAL_COLOR                    [CCColor whiteColor]
#define DISABLED_COLOR                  [CCColor grayColor]
#define DARK_COLOR                      [CCColor darkGrayColor]
#define BLUR_COLOR                      [CCColor grayColor]
#define TEXT_COLOR                      [CCColor colorWithRed:0.8 green:0.8 blue:0.1]
#define DETAIL_TEXT_COLOR               [CCColor colorWithRed:0.7 green:0.6 blue:0.5]
#define SKILL_BUTTON_TEXT_COLOR         [CCColor colorWithRed:0.7 green:0.7 blue:0.7]
#define DESCRIPTION_TEXT_COLOR          [CCColor colorWithRed:1.0 green:0.9 blue:0.0]
#define HISTORY_TEXT_COLOR              [CCColor colorWithRed:1.0 green:0.9 blue:0.9]
#define PROMPT_TEXT_COLOR               [CCColor colorWithRed:1.0 green:0.9 blue:0.9]



#define SWIPE_DURATION(duration)                MIN(MAX(duration, DURATION_SWIPE_MINIMUM), DURATION_SWIPE_MAXIMUM)
#define PLAYING_CARD_PADDING(count, max)        (count > max) ? -(PLAYING_CARD_WIDTH*(count-max) / (count-1)) : 0.0f
#define CARD_MOVE_POSITION(pos, idx, count)     ccpAdd(pos, ccp((NSInteger)(idx+1-count+idx)*PLAYING_CARD_WIDTH/4, 0.0f))
#define FILE_FULL_NAME(name, num, type)         [[NSString stringWithFormat:@"%@%tu", name, num] stringByAppendingPathExtension:type]

#endif
