
import React from 'react'

function LibraryButton({buttonLabel, onClickButton}) {

  return (
    <div>
      <button onClick={onClickButton}>
        {buttonLabel}
      </button>
    </div>
  )
}

export default LibraryButton
