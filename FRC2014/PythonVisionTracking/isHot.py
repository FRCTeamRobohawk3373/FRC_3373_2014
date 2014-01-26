# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4

import SimpleCV

DEBUG = True

screenSize = (800, 600)
display = SimpleCV.Display(screenSize)

camURL = "http://192.168.0.90:80/jpg/image.jpg"

ratioLow = 1.1
ratioHigh = 1.6

def main():
    isHot = False
    while display.isNotDone():

        drawingLayer = SimpleCV.DrawingLayer(screenSize)

        img = SimpleCV.Image(camURL)

        greenDist = img.colorDistance(SimpleCV.Color.AQUAMARINE)
        filtered = img - greenDist

        blobs = filtered.findBlobs()#thresheval = 80, minsize = 300

        if blobs:
            rectangles = blobs.filter([b.isRectangle(0.3) for b in blobs])
            if rectangles:
                
                drawRects(rectangles, filtered, drawingLayer)
                isHot = checkIsHot(rectangles, isHot)
                if DEBUG: debugPrint(rectangles, isHot)

                filtered.applyLayers()

        filtered.save(display)


def debugPrint(rectangles, isHot):
    if len(rectangles) > 1:
        rec1H = float(rectangles[-1].height())
        rec2W = float(rectangles[-2].width())
        print("ratio: " + str(rec1H / rec2W))
    print("isHot: " + str(isHot))
    print("distance: " + str(rectangles[-1].height()))

def drawRects(rectangles, filtered, drawingLayer):
    drawingLayer.centeredRectangle(rectangles[-1].coordinates(), (rectangles[-1].width(), rectangles[-1].height()), SimpleCV.Color.YELLOW, 1, False, -1)
    if len(rectangles) > 1:
        drawingLayer.centeredRectangle(rectangles[-2].coordinates(), (rectangles[-2].width(), rectangles[-2].height()), SimpleCV.Color.YELLOW, 1, False, -1) 
    filtered.addDrawingLayer(drawingLayer)

def checkIsHot(rectangles, isHot):
    if len(rectangles) > 1:
        rec1H = float(rectangles[-1].height())
        rec2W = float(rectangles[-2].width())
        ratio = rec1H / rec2W

        isHot = (ratio > ratioLow) and (ratio < ratioHigh)
        #print("isHot live: " + str(isHot))
    else:
        isHot = False

    return isHot

main()

