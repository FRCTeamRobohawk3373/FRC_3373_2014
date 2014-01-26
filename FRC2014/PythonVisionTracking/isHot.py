# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4

import SimpleCV

screenSize = (800, 600)
display = SimpleCV.Display(screenSize)

camURL = "http://192.168.0.90:80/jpg/image.jpg"

isHot = False
ratioLow = 5.0
ratioHigh = 6.0

while display.isNotDone():

    drawingLayer = SimpleCV.DrawingLayer(screenSize)

    img = SimpleCV.Image(camURL)

    greenDist = img.colorDistance(SimpleCV.Color.AQUAMARINE)
    filtered = img - greenDist

    blobs = filtered.findBlobs()#thresheval = 80, minsize = 300

    if blobs:
        rectangles = blobs.filter([b.isRectangle(0.3) for b in blobs])
        if rectangles:
            
            debugPrint()

            drawingLayer.centeredRectangle(rectangles[-1].coordinates(), (rectangles[-1].width(), rectangles[-1].height()), SimpleCV.Color.YELLOW, 1, False, -1)
            filtered.addDrawingLayer(drawingLayer)
            filtered.applyLayers()

            
            #TODO Fix the problem when there is only one rectangle in the array
            #if ((rectangles[-1].height() * rectangles[-2].width()) > ratioLow) and ((rectangles[-1].height() * rectangles[-2].width()) < ratioHigh):
            #    isHot = True
            #else:
                isHot = False
    filtered.save(display)


def debugPrint():
    print(rectangles[-1].height() * rectangles[-2].width())
    print(isHot)

   
